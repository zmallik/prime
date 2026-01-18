
# DROPBOX /  FILE STORAGE SYSTEM


> Chunking files </br>
> Metadata service </br>
> Upload/download flow </br>
> Consistency </br>
> Deduplication </br>

- pre-signed urls

1) How can you support large files?
```
> 50GB => (50 * 80)/100 = 4000 sec (100 Mbps connection) = 1 hour. 11 ssec
> A fingerprint is a mathematical calculation that generates a unique hash value based on the content of the file.
This hash value, often created using cryptographic hash functions like SHA-256
> Each chunk gets an ETag upon successful upload, which the client can include in the PATCH request to our backend.
Our backend can then verify these ETags by calling S3's ListParts API, providing an efficient way
to validate multiple chunks at once. 
> chunking
> Amazon s3 multipart upload
> we can also utilize compression to speed up both uploads and downloads
```

2) How can we make uploads, downloads, and syncing as fast as possible?
> compression using gzip etc
3) How can you ensure file security?
> a) encryption at transit (HTTPS), b) encription at rest in S3
 

