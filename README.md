## Feature http-client documentation

- [Micronaut Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)

## Feature openapi documentation

- [Micronaut OpenAPI Support documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/index.html)

- [https://www.openapis.org](https://www.openapis.org)


openssl pkcs12 -export -in client-2048.crt -inkey client-2048.key -out client-2048.p12



1. Finish Betfair API / CLient / Model
2. Create rudimentary publish subscribe layer (micronaut??) interface to betfair
3. http-monitor
    read "READ MARKET FINDERS".... go find markets (Date-Bounded, Sport bounded event)
    read "Read MARKET MONITORS"....
           if time to read, read and publish data
    read "Traders"
          listen for publish data
          analyse whether an order should be made
          analyse if any order needs closed

http - monitor
    - read strategeies
    - decide whether to run (criteria rate-limit, strat limit)
        if (yes)
            get latest prices
            update price history
            decide to place order


