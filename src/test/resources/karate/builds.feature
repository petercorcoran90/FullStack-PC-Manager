Feature: PC Build Management API Tests
  To verify build creation, part management, and total cost calculation

Background: Setup Data for Builds
  Given url baseUrl
  
  # 1. Setup User Auth
  * def testUsername = 'builder_' + java.util.UUID.randomUUID().toString().substring(0,8)
  Given path '/api/users/register'
  And request { "username": '#(testUsername)', "password": "password", "role": "ROLE_USER" }
  When method post
  
  Given path '/api/users/login'
  And request { "username": '#(testUsername)', "password": "password" }
  When method post
  * def userAuth = { Authorization: '#("Bearer " + response.token)', Accept: 'application/json' }

  # 2. Create a Part as Admin
  Given path '/api/users/login'
  And request { "username": "admin", "password": "admin" }
  When method post
  * def adminAuth = { Authorization: '#("Bearer " + response.token)' }

  * def testRamName = 'Test RAM ' + java.util.UUID.randomUUID().toString().substring(0,8)
  Given path '/api/parts'
  And headers adminAuth
  And request { "name": '#(testRamName)', "manufacturer": "Corsair", "category": "RAM", "price": 80.00, "stockLevel": 20 }
  When method post
  * def testPartId = response.id

  # 3. Create a Base Build for the User
  * def baseBuildName = 'Base Gaming PC ' + java.util.UUID.randomUUID().toString().substring(0,8)
  Given path '/api/builds'
  And headers userAuth
  And param username = testUsername
  And param name = baseBuildName
  When method post
  * def baseBuildId = response.id

# --- SCENARIOS ---

Scenario: Create a new Build Profile
  Given path '/api/builds'
  And headers userAuth
  And param username = testUsername
  And param name = 'Office PC'
  When method post
  Then status 201
  And match response.buildName == 'Office PC'

Scenario: Add a single part to a build
  Given path '/api/builds/' + baseBuildId + '/parts/' + testPartId
  And headers userAuth
  When method post
  Then status 200
  And match response.parts == '#[1]'
  And match response.totalPrice == 80.00

Scenario: Add duplicate parts to a build and calculate total
  # Add first part
  Given path '/api/builds/' + baseBuildId + '/parts/' + testPartId
  And headers userAuth
  When method post
  # Add second duplicate part
  Given path '/api/builds/' + baseBuildId + '/parts/' + testPartId
  And headers userAuth
  When method post
  Then status 200
  And match response.parts == '#[2]'
  And match response.totalPrice == 160.00

Scenario: Remove a part from a build
  # Setup: Add the part first
  Given path '/api/builds/' + baseBuildId + '/parts/' + testPartId
  And headers userAuth
  When method post
  
  # Action: Remove it
  Given path '/api/builds/' + baseBuildId + '/parts/' + testPartId
  And headers userAuth
  When method delete
  Then status 200
  And match response.parts == '#[0]'
  And match response.totalPrice == 0.00