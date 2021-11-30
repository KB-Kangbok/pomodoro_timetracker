### CI/CD status

CI/CD is fully working for Web 2

### Installation

## Web client

1. Navigate to PTTWeb2/client
2. Run `npm i`
3. Change backend API url in src/config.json
4. To run the web client, `npm start`
5. To build, `npm run build`

## Web testing

1. Navigate to PTTWeb2/web2testing
2. Change frontend url in `src/test/java/edu/gatech/BrowserFunctions.java`, `baseUrl`
3. Run `./mvnw test`

