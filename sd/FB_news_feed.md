#  Design a Feed System (Twitter / Instagram / Facebook)

High signal question

Key topics

> Fan-out on write vs read </br>
> Timeline generation  </br>
> Ranking </br>
> Caching strategy </br>
> Storage choice (Cassandra) </br>


## Functional Requirements
### Core Requirements
1. Users should be able to create posts.
2. Users should be able to friend/follow people.
3. Users should be able to view a feed of posts from people they follow, in chronological order.
4. Users should be able to page through their feed.

Below the line (out of scope):
1. Users should be able to like and comment on posts.
2. Posts can be private or have restricted visibility.

### Non-Functional Requirements
Core Requirements
The system should be highly available (prioritizing availability over consistency). We'll tolerate up to 1 minute of post staleness (eventual consistency).
Posting and viewing the feed should be fast, returning in < 500ms.
The system should be able to handle a massive number of users (2B).
Users should be able to follow an unlimited number of users, users should be able to be followed by an unlimited number of users.

### Core Entities
User: A users in our system.
Follow: A uni-directional link between users in our system.
Post: A post made by a user in our system. Posts can be made by any user, and are shown in the feed of users who follow the poster.

### API 

```
POST /posts 
{
    "content": { }
}
// -> 200 OK
```


```
{
    "postId": // ...
}
```

```
PUT /users/[id]/followers
{ } 
// -> 200 OK
```

```
GET /feed?pageSize={size}&cursor={timestamp?}
{
    items: Post[],
    nextCursor: string
}
```


 ## 1. Users should be able to create posts.

<img width="1344" height="375" alt="Screenshot 2025-12-03 at 5 19 39 PM" src="https://github.com/user-attachments/assets/061bb26b-7673-43d8-9005-d7003c9448b3" />

 ## 2.  Users should be able to friend/follow people.

<img width="1320" height="518" alt="Screenshot 2025-12-03 at 5 21 16 PM" src="https://github.com/user-attachments/assets/c35e4e0d-5931-495a-88de-4a9f473d65a6" />

## 3. Users should be able to view a feed of posts from people they follow.

a.First, we get all of the users who a given user is following.
b. Next, we get all of the posts from those users.
c. Finally, we sort all those posts by time and return them to the user.

## 4. Users should be able to page through their feed.

First, we get all of the users who a given user is following.
Next, we get all of the posts from those users that are older than the input timestamp.
Finally, we sort all those posts by time and return them to the user.

# Potential Deep Dives

## 1) How do we handle users who are following a large number of users?

For users following a large number of users we can keep a PrecomputedFeed table. Instead of querying the Follow and Post tables, we'll be able to pull from this precomputed table. Then, when a new post is created, we'll simply add to the relevant feeds.
The PrecomputedFeed table itself is just a list of post IDs, stored in chronological order, and limited to a small number of posts (say 200 or so). We want this table to be compact so we can minimize the amount of space we require. We'll use a partition key of the userId of the feed and its value will be a list of post IDs in order. Since we only ever access this table by user ID, we don't need to deal with any secondary indexes.

## 2) How do we handle users with a large number of followers?
 A great option would extend on Async Workers outlined above. We'll create async feed workers that are working off a shared queue to write to our precomputed feeds.
But we can be more clever here: we can choose which accounts we'd like to pre-calculate into feeds and which we do not.
For Justin Bieber (and other high-follow accounts), instead of writing to 90+ million followers we can instead add a flag onto the Follow table which indicates that this particular follow isn't precomputed. In the async worker queue, we'll ignore requests for these users.
On the read side, when users request their feed via the Feed Service, we can grab their (partially) precomputed feed from the Feed Table and merge it with recent posts from those accounts which aren't precomputed.
This hybrid approach allows us to choose whether we fanout on read or write and for most users we'll do a little of both. This is a great system design principle! In most situations we don't need a one-size-fits-all solution, we can instead come up with clever ways to solve for different types of problems and combine them together.
Challenges
Doing the merging of feeds at read time vs at write time means more computation needs to be done in the Feed Service. We can tune the threshold over which an account is ignored in precomputation.

<img width="1294" height="727" alt="Screenshot 2025-12-03 at 5 27 08 PM" src="https://github.com/user-attachments/assets/1dc1c979-36ec-4b1d-8780-e1fde4ea846d" />

## 3) How can we handle uneven reads of Posts?
   Like the Distributed Post Cache above, a great solution for this problem is to insert a cache between the readers of the Post table and the table itself. Since posts are very rarely edited, we can keep a long time to live (TTL) on the posts and have our cache evict posts that are least recently used (LRU). As long as our cache is big enough to house our most popular posts, the vast majority of requests to the Post Table will instead hit our cache.
Unlike the Distributed Post Cache solution above, we can choose to have multiple distinct caches that our readers can hit. These cache instances don't need to coordinate. Instead of distributing the posts in our cache across the entire fleet, we can have multiple instances which can all service the same postID. This does mean we may have more requests that end up going to our database (for a very viral post, with N cache instances, we might have N requests to the database instead of 1 if we had sharded the cache by postID). But N requests is much, much smaller than the millions of requests we'd need to handle if we didn't have a cache in the first place.
Both solve the problem, but this solution means we have N times the throughput for a hot key without any additional coordination required.
<img width="1350" height="764" alt="Screenshot 2025-12-03 at 5 27 31 PM" src="https://github.com/user-attachments/assets/baae0814-3563-4b89-941a-26da00d085ef" />


References:
[Hello interview](https://www.hellointerview.com/learn/system-design/problem-breakdowns/fb-news-feed). 
