# VIES_Check
This is a simple Java package to check EU VAT numbers by using the [VIES Database](http://ec.europa.eu/taxation_customs/vies/vatRequest.html)
and their [SOAP API](http://ec.europa.eu/taxation_customs/vies/checkVatTestService.wsdl).

To check if VAT number is a valid format:

```java
import vat.ViesValidation
boolean ValidFormat = ViesValidation.ValidFormat(args, "GB33333333");
```

To check if a VAT number is valid per the VIES database:
```java
import vat.ViesValidation
boolean ValidNumber = ViesValidation.ValidNumber(args, "GB", "3333333");
```
