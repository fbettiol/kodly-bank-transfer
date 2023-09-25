# kodly-bank-transfer
Bank transfer system - interview process


# Implementation
I have implemented the transfer interface in a new controller (/v1/transfer/) because I consider that the accountController (/v1/account/) is meant for the CRUD operations of the accounts.
In order to avoid deadlocks and race conditions while transferring money, I have implemented a MemoryLockHandler - which is basically responsible for acquiring/releasing locks based on account keys.


# Future improvements 
#### 1) The current implementation does not scale up horizontally. There are two basic reasons: the first one is that everything is kept on memory, therefore each replica would have its own accounts. The second one is that, even if we were to synchronize the map holding the accounts' info, we would still need a mechanism to handle distributed locks to make sure that race-conditions do not occur.    
    - Add a database in order to be able to persist account information 
    - Add distributed lokcing support (Spring-integration, Zookeepr, etc).

#### 2) Add two-factor authentication and security (depending on how it is deployed).
    - It might be done by another microservice before calling this one.

#### 3) Separate the "domain" package into its own repo/module. The client of this API only needs the DTOs, not the whole microservice.

#### 4) Add an exception handling mechanism to provide uniform errors to the clients when they happen. It could be achieved by using the Spring annotations @ControllerAdvice and @ExceptionHandler

#### 5) When a transaction is completed, it should be stored in some kind of auditory table in order to be able to trace the activity. 

#### 6) The lock handler does not free the memory. It should have an atomic counter and, when it reaches zero, it should be deleted from the map. 