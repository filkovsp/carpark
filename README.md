# Basic Parking Application
MVP:
- When the car comes in, check if there is a space and allocate space if available, otherwise give message saying full.   Let us assume the car park size is 100.
- When the car leaves, calculate the time spent and charge £2 per hour.  We don’t need the payment module but just returning the amount is enough.
- Handle multiple cars coming at the same time.

## Implementation

### Vehicle:
Vehicle supposed to have variations by:
- Engine Type: COMBUSTION | ELECTRICAL 
- Wheel Base : 2-Wheeler : 4-Wheeler.  

### Car Park Service: 
Handles requests to park and unpark a vehicle. Methods `park()` and `leave()` are synchronized for thread safety.
Car Park consists of storeys, that implements Synchronized thread-safe List. 
This is successfully tested in `CarParkApplicationTest` class by creating to concurrent threads that are trying to park `free` vehicles.

### Payment Service:
Calculates parking cost based on Duration of the Parking Session.

### Persistence:
Persistence implemented with Spring Data JPA ORM, and for simplicity support H2 in memory db.

### Assumptions:
It's assumed that there are physical gates that read plate numbers from vehicles and produce printed tickets for them.
Drivers supposed to park their vehicles according to the given place.
On exit, they should pay for the parking service.
Same vehicle can't be parked twice, so the system will reject to issue parking ticket for it.
If there is an error in reading plate number of departing vehicle, system will throw an error as well.
When issuing a parking ticket system doesn't deal with reserved parking space at the moment, instead it picks parking space randomly at any storey level.  

Currently, application doesn't deal with EV. However, there is room to handle that. 
Most simple way if dealing with EV/non-EV is:
- either not to let vehicles with combustion engine to take EV Enabled parking spaces
- or, let vehicles with combustion engine to park at EV Enabled spaces but apply surcharge.
Application assumes there are no issues with payment transactions but this is still can be handled in the Payment service.