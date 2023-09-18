# Basic (Robotic) Parking Application
MVP:
- When the car comes in, check if there is a space and allocate space if available, otherwise give message saying full.   Let us assume the car park size is 100.
- When the car leaves, calculate the time spent and charge £2 per hour.  We don’t need the payment module but just returning the amount is enough.
- Handle multiple cars coming at the same time.

## Implementation

### Vehicle:
Vehicle supposed to have variations by:
- Engine Type: `COMBUSTION` | `ELECTRICAL` 
- Wheel Base: `2-Wheeler` | `4-Wheeler`.  

### Car Park Service: 
Handles requests to park and unpark a vehicle. Methods `park()` and `leave()` are synchronized for thread safety.
Car Park consists of storeys, that implements Synchronized thread-safe List. 
This is successfully tested in `CarParkApplicationTest` class by creating to concurrent threads that are trying to park available vehicles whose number exceeds the parking capacity.

### Payment Service:
Calculates parking cost based on the Duration of the Parking Session and logs it to the user.

### Persistence:
Persistence implemented with Spring Data JPA ORM, and for simplicity supports H2 in memory db.

### MVP Assumptions:
It's been assumed this is an automated robotic Car Park. There is parking portal equipped with reading camera to read the plate number from a vehicle.
With the parking process fully automated, once driver puts their car into the portal and presses `Park` button, robot parks the vehicles according to the allocated place.
Portal has a gate system, that will only open if car park has free spaces. At the Car Park's building we can have a digital display showing how many available spaces present there at the moment. 
Before un-parking, driver requests their vehicle back from parking service by vehicles Registration Number, it triggers payment service that updates parking session details. 
It's been assumed the actual bank payment will be done later through the Direct Debit service based on the monthly parking stats.   

Same vehicle can't be parked twice, so the system will reject parking for it.
Vehicle that doesn't have proper parking session can't be un-parked and system will throw an error if such event occurs.
When allocating a parking space system doesn't deal with reserved (dedicated) parking space at the moment, instead it just picks parking space randomly at any storey level.  

Currently, application doesn't deal with EV. However, there is room to handle that. 
The most simple way dealing with EV/non-EV vehicles is:
- either not to let vehicles with combustion engine to take EV Enabled parking spaces
- or, let vehicles with combustion engine to park at EV Enabled spaces but apply surcharge (requires driver's consent).
- or assume that each parking slot is EV-Enabled by default, and only in case of technical fault EV Capability supposed to be turned off manually (temporarily faulty EV points should be listed in some repository, similar to Parking Session Repository).

### Further improvements:
Currently, system assumes that gates are not letting vehicles in unless another vehicle completely left the parking. 
For this purpose, our Synchronized List of parking spaces works fine. 
To make system more effective and allow it to park incoming vehicles faster we can introduce two Queues: 
- Parking Queue
- Leaving Queue

This will also need some messaging pattern to make Queue Service Observable. 
As soon as change in any of the queues happened, a queue watcher should start in a separate thread and either let another vehicle park from the Parking Queue, 
or update (close) existing Parking Session for the vehicle that has just left the Leaving Queue.

In case if we want implement traditional parking process close to our real world abstraction, where human interacts with the system,
we should accept that vehicle might stay unpredictable time in the Parking Queue and that should count into its parking charge. 
As well as human can try and cheat the system when leaving car park, so we might want to reject the leaving gate to open if 
vehicle has spent in the Leaving Queue longer than some threshold time, and make driver pay extra cost for that.

Additional services could be added:
- Reservation Service (parking on demand)
- Car Washing Service - while car is being washed, it's in a parking queue. If driver decides to leave parking within 5 minutes after their vehicle has been washed, they won't be charged for parking.  

![concept](./concept.png)