# User management service

This is a demo app to test the following technical stack:

- Java 17+
- Spring Boot
- Spring Web Flux
- Spring Data JDBC
- Reactor
- PostgreSQL
- Spock

## Application requirements

We must develop the user management service, that allows us to manage basic operations with User and Customer domain
objects, as well as authentication and customer registration.

The service will have the 2 main entities:

- User - represents the identity that is used for authentication and contains the user type (ADMIN,MANAGER,CUSTOMER).
- Customer - user extension, the "application end customer" or "public system user", that includes
  additional data.

### User data:

- id
- login
- name
- surname
- email
- password
- type - ADMIN/MANAGER/CUSTOMER

### Additional Customer data:

- date of birth
- country of residence
- identity number
- passport number
- registration IP address (not exposed to customer)
- registration country (not exposed to customer)

### Common REST endpoints (will be used in all client apps):

### 1. POST /auth - JWT authentication. Returns JWT token.

### Private REST endpoints (will be used ony in internal administration application):

All private endpoints require JWT auth.

#### 1. POST /user - create new user.

Supported types are only ADMIN/MANAGER.

#### 2. GET /user/{:id} - get user data by id.

Return the User or Customer object data.

#### 3. PUT /user/{:id} - update user data by id.

Supported types are only ADMIN/MANAGER.

#### 4. DELETE /user/{:id} - delete user by id.

#### 5. GET /users?offset={:offset}&limit={:limit}

Return a pageable list of users (user type is ADMIN/MANAGER) with specified offset and limit.

#### 6. GET /customers?offset={:offset}&limit={:limit}

Return a pageable list of customers (customer type is CUSTOMER) with specified offset and limit.

#### 7. GET /me - get current(authenticated) user.

### Public REST endpoints (will be used in public web/mobile apps):

All customer object responses must not contain "id" field.

#### 1. POST /registration - register a new customer (no auth)

#### 2. GET /customer/profile/{:login} - get any customer public profile by login (no auth).

Show only login/name/surname/date of birth and country of residence.

#### 3. GET /customer/my/profile - get current (authenticated) customer's profile (JWT auth).

Show all data except id and password.

#### 4. PUT /customer/my/profile - update current (authenticated) customer's profile by id (JWT auth).
