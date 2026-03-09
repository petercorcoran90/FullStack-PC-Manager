Feature: Hardware Part API Tests
  To test the CRUD operations for Hardware Parts

Background: Setup the Base path and Authentication
  Given url baseUrl
  
  # 1. Register a test user dynamically
  Given path '/api/users/register'
  And request { "username": "hardwaretester", "password": "password", "role": "ROLE_ADMIN" }
  When method post
  
  # 2. Login to get the JWT Token
  Given path '/api/users/login'
  And request { "username": "hardwaretester", "password": "password" }
  When method post
  Then status 200
  * def authToken = response.token
  
  # 3. Configure Karate to send this token with EVERY request in this file!
  * def authHeader = 'Bearer ' + authToken
  * configure headers = { Authorization: '#(authHeader)', Accept: 'application/json' }

Scenario: Create, Read, Update, and Delete a Hardware Part
  # 1. Create a new part (POST)
  Given path '/api/parts'
  And request { "name": "Karate GPU", "manufacturer": "NVIDIA", "category": "GPU", "price": 599.99, "stockLevel": 15 }
  When method post
  Then status 201
  And match response.id == '#present'
  And match response.id == '#number'
  And match response.name == 'Karate GPU'
  
  # Save the auto-generated ID for the next steps!
  * def partId = response.id

  # 2. Get Part by ID (GET)
  Given path '/api/parts', partId
  When method get
  Then status 200
  And match response.name == 'Karate GPU'
  And match response.price == 599.99

  # 3. Update Part (PUT)
  Given path '/api/parts', partId
  And request { "name": "Karate GPU Pro", "manufacturer": "NVIDIA", "category": "GPU", "price": 699.99, "stockLevel": 10 }
  When method put
  Then status 200
  And match response.name == 'Karate GPU Pro'
  And match response.price == 699.99

  # 4. Search Parts using Request Param (GET)
  Given path '/api/parts'
  And param search = 'Karate'
  When method get
  Then status 200
  And match response[0].id == '#present'
  And match response[0].name == 'Karate GPU Pro'

  # 5. Delete Part (DELETE)
  Given path '/api/parts', partId
  When method delete
  Then status 204
  
  Scenario: Missing or Invalid Fields - Unsuccessful
  # Attempt to create a part with negative price and stock
  Given path '/api/parts'
  And request { "name": "Faulty RAM", "manufacturer": "Corsair", "category": "RAM", "price": -10.00, "stockLevel": -5 }
  When method post
  # Expecting a failure status (usually 400 Bad Request or 500 depending on your exception handler)
  # Karate allows us to assert that the status is NOT 201 Created
  Then assert responseStatus == 400 || responseStatus == 500

Scenario: Unauthorised Access Attempt
  # 1. Clear the Authorization header that was set in the Background step
  * configure headers = { Accept: 'application/json' }
  
  # 2. Attempt to add a part WITHOUT a JWT token
  Given path '/api/parts'
  And request { "name": "Hacker GPU", "manufacturer": "NVIDIA", "category": "GPU", "price": 100.00, "stockLevel": 1 }
  When method post
  
  # 3. System should reject the request (401 Unauthorized or 403 Forbidden)
  Then assert responseStatus == 401 || responseStatus == 403