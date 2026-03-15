Feature: Hardware Part API Tests
  To test the CRUD operations for Hardware Parts

Background: Setup Auth and Base Data
  Given url baseUrl
  
  # 1. Login as Admin
  Given path '/api/users/login'
  And request { "username": "admin", "password": "admin" }
  When method post
  Then status 200
  * def adminAuth = { Authorization: '#("Bearer " + response.token)', Accept: 'application/json' }
  * configure headers = adminAuth

  # 2. Create a Base Part before EVERY scenario to guarantee data exists
  * def basePartName = 'Base GPU ' + java.util.UUID.randomUUID().toString().substring(0,8)
  Given path '/api/parts'
  And request { "name": '#(basePartName)', "manufacturer": "NVIDIA", "category": "GPU", "price": 500.00, "stockLevel": 10 }
  When method post
  Then status 201
  * def existingPartId = response.id

# --- SCENARIOS ---

Scenario: Create a new Hardware Part (POST)
  Given path '/api/parts'
  And request { "name": "New Karate GPU", "manufacturer": "AMD", "category": "GPU", "price": 499.99, "stockLevel": 5 }
  When method post
  Then status 201
  And match response.name == 'New Karate GPU'

Scenario: Retrieve an existing Part by ID (GET)
  Given path '/api/parts/' + existingPartId
  When method get
  Then status 200
  And match response.name == basePartName

Scenario: Delete a Part (DELETE)
  Given path '/api/parts/' + existingPartId
  When method delete
  Then status 204

Scenario: Prevent creating a part with a negative price
  Given path '/api/parts'
  # Valid stock level, invalid negative price
  And request { "name": "Faulty RAM", "manufacturer": "Corsair", "category": "RAM", "price": -10.00, "stockLevel": 5 }
  When method post
  Then status 400
  And match response.message == 'Price must be greater than zero.'

Scenario: Prevent creating a part with a negative stock level
  Given path '/api/parts'
  # Valid price, invalid negative stock level
  And request { "name": "Faulty RAM 2", "manufacturer": "Corsair", "category": "RAM", "price": 50.00, "stockLevel": -5 }
  When method post
  Then status 400
  And match response.message == 'Stock level cannot be negative.'

Scenario: Prevent unauthorized access to Admin endpoints
  * configure headers = { Accept: 'application/json' } # Clear the token
  Given path '/api/parts'
  And request { "name": "Hacker GPU", "manufacturer": "NVIDIA", "category": "GPU", "price": 100.00, "stockLevel": 1 }
  When method post
  Then assert responseStatus == 401 || responseStatus == 403

Scenario: US6 AC1 - Successfully update an existing part
  # 1. Update the base part with new values
  Given path '/api/parts/' + existingPartId
  And request { "name": "Updated Base GPU", "manufacturer": "NVIDIA", "category": "GPU", "price": 600.00, "stockLevel": 20 }
  When method put
  Then status 200
  And match response.name == 'Updated Base GPU'
  And match response.price == 600.00

Scenario: US6 AC2 - Prevent updating a part with a negative price
  # 1. Try to update the base part with a negative price
  Given path '/api/parts/' + existingPartId
  And request { "name": "Valid Name", "manufacturer": "AMD", "category": "GPU", "price": -50.00, "stockLevel": 10 }
  When method put
  
  # 2. Assert the GlobalExceptionHandler catches it and returns 400 Bad Request
  Then status 400
  And match response.message == 'Price must be greater than zero.'