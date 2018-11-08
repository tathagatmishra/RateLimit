# RateLimit
Implementation of RateLimit Api



API Rate Limit :

Hotels-api is a sprint boot app, which allows

Users to access hotel details based on City Id.

localhost:8080/key/jkl/hotels/Ashburn/

User can also sort results based on price, both ascending and descending

http://localhost:8080/key/jkl/hotels/Ashburn/sort/DESC

http://localhost:8080/key/jkl/hotels/Ashburn/sort/ASC

API access is limited to preset value set in ratelimit.properties file against each api key. If api key is not present in ratelimit.properties file, it takes default limit from global key present in the same file.

Implementation of RateLimit ;

RateLimitCache : Cache having key as apiKey and value as a TreeSet of timestamps when apikey was used.

BlockedApiKeysCache : Cache having key as ApiKey and value as the timestamp when apiKey was blocked for exceeding rate limit.

1) On each access by a apiKey, it is checked if it is present in BlockedApiKeysCache.

2) If there is any entry, corresponding timestamp value is checked to ensure if timestamp value is with in previous 5 minutes.

3) If value is with in 5 minutes, apiKey is restricted with HttpStatus.TOO_MANY_REQUESTS response.

4) If value is more than 5 minutes old, apKey is removed from RateLimitCache and apiKey proceeds with further checks.

5) Proceeding further it is checked if there are timestamps stored in RateLimitCache that are more than 10 seconds w.r.t to current timestamp. All such instances are removed.

6) Then it is checked, if inserting current timestamp will exceed the timelimit. If it exceeds, apiKey is blocked by adding its entry in BlockedApiKeysCache and HttpStatus.TOO_MANY_REQUESTS is sent.

7) If limit is not reached, access is granted and entry is made in RateLimitCache.
