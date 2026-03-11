Feature: Hardware Inventory Analytics API Tests
  To verify data aggregation and role-based access for inventory charts (User Story 5)

Background: Setup Auth and Base Data
  Given url baseUrl
  
  # 1. Setup Admin Token
  Given path '/api/users/login'
  And request { "username": "admin", "password": "admin" }
  When method post
  Then status 200
  * def adminAuthHeader = { Authorization: '#("Bearer " + response.token)', Accept: 'application/json' }

  # 2. Setup Customer Token
  * def testCustomer = 'customer_' + java.util.UUID.randomUUID().toString().substring(0,8)
  Given path '/api/users/register'
  And request { "username": '#(testCustomer)', "password": "password", "role": "ROLE_USER" }
  When method post
  
  Given path '/api/users/login'
  And request { "username": '#(testCustomer)', "password": "password" }
  When method post
  * def customerAuthHeader = { Authorization: '#("Bearer " + response.token)', Accept: 'application/json' }

  # 3. Add a part as Admin to guarantee data exists for the chart aggregation
  * def analyticsPartName = 'Analytics GPU ' + java.util.UUID.randomUUID().toString().substring(0,8)
  Given path '/api/parts'
  And headers adminAuthHeader
  And request { "name": '#(analyticsPartName)', "manufacturer": "NVIDIA", "category": "GPU", "price": 500.00, "stockLevel": 15 }
  When method post
  Then status 201

# --- SCENARIOS ---

Scenario: Admin successfully retrieves aggregated stock analytics (AC1)
  Given path '/api/analytics/stock'
  And headers adminAuthHeader
  When method get
  Then status 200
  
  # Verify schema matches CategoryStockDTO
  And match response == '#[]'
  And match each response contains { category: '#string', totalStock: '#number' }
  
  # Verify aggregation math
  * def gpuData = karate.filter(response, function(x){ return x.category == 'GPU' })
  * assert gpuData[0].totalStock >= 15

Scenario: Customer is denied access to analytics endpoint (AC4)
  Given path '/api/analytics/stock'
  And headers customerAuthHeader
  When method get
  Then status 403