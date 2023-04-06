### How to run

By docker

1. build image for docker
   `gradle clean bootBuildImage`
2. run dokcer DB(postgres) and builded app
   `docker compose -f ./docker/docker-compose.yaml up`

### Swagger

Swagger available by link (after application startup) :
[/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Get JWT TOKEN

for getting token you can use curl example. Token will be in response. Most endpoints required authentication, you
should send token in Bearer header .

`curl -X POST --location "http://localhost:8080/token" \
-H "Content-Type: application/x-www-form-urlencoded" \
--basic --user test@email.com:password`

### Predefined users

| User ROLE | username          | password |
|-----------|-------------------|----------|
| USER      | test@email.com    | password |
| ADMIN     | admin@email.com   | admin    |
| COURIER   | courier@email.com | courier  |

### User stories

#### 1.User

| Task                                                    | Done | Endpoint                                                                  |
|---------------------------------------------------------|------|---------------------------------------------------------------------------|
| Can create an user account and log in;                  | +    | [/user/v1/create](http://localhost:8080/user/v1/create)                   |
| Can create a parcel delivery order;                     | +    | [/parcel/v1/create](http://localhost:8080/parcel/v1/create)               |
| Can change the destination of a parcel delivery order;  | +    | [/parcel/v1/updateAddress](http://localhost:8080/parcel/v1/updateAddress) |
| Can cancel a parcel delivery order;                     | +    | [/parcel/v1/updateStatus](http://localhost:8080/parcel/v1/updateStatus)   |
| Can see the details of a delivery;                      | +    | [/parcel/v1/get](http://localhost:8080/parcel/v1/get)                     |
| Can see all parcel delivery orders that he/she created; | +    | [/parcel/v1/getAll](http://localhost:8080/parcel/v1/getAll)               |

#### 2.Admin

| Task                                              | Done | Endpoint                                                                |
|---------------------------------------------------|------|-------------------------------------------------------------------------|
| Can change the status of a parcel delivery order; | +    | [/parcel/v1/updateStatus](http://localhost:8080/parcel/v1/updateStatus) |
| Can view all parcel delivery orders;              | +    | [/parcel/v1/getAll](http://localhost:8080/parcel/v1/getAll)             |
| Can assign parcel delivery order to courier;      | +    | [/user/v1/assign](http://localhost:8080/user/v1/assign)                 |
| Can log in and create a courier account;          | +    | [/user/v1/create](http://localhost:8080/user/v1/create)                 |
| -- Can see list of couriers with their statuses;  | -    | Not Ready, because of not enough information.                           |
| -- Can track the delivery order by coordinates;   | -    | Not Ready, because of not enough information.                           |

#### 3.Courier

| Task                                                      | Done | Endpoint                                                                |
|-----------------------------------------------------------|------|-------------------------------------------------------------------------|
| Can log in;                                               | +    | [/token](http://localhost:8080/token)                                   |
| Can view all parcel delivery orders that assigned to him; | +    | [/parcel/v1/getAll](http://localhost:8080/parcel/v1/getAll)             |
| Can change the status of a parcel delivery order;         | +    | [/parcel/v1/updateStatus](http://localhost:8080/parcel/v1/updateStatus) |
| Can see the details of a delivery order;                  | +    | [/parcel/v1/get](http://localhost:8080/parcel/v1/get)                   |