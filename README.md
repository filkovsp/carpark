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

### MVP Assumptions:
It's assumed that there are physical gates that read plate numbers from vehicles and produce printed tickets for them.
This is a robotic Car Park, so the parking process is fully automated and robot always parks the vehicles according to the allocated places.
Before un-parking, driver requests their vehicle back from parking service, it triggers payment service that updates parking session details, presumably that actual bank payment will done through Direct Debit based on monthly parking stats.   

Same vehicle can't be parked twice, so the system will reject to issue parking ticket for it.
Vehicle that doesn't have proper parking session can't be un-parked and system will throw an error if such event occurs.
When issuing a parking ticket system doesn't deal with reserved (dedicated) parking space at the moment, instead it picks parking space randomly at any storey level.  

Currently, application doesn't deal with EV. However, there is room to handle that. 
The most simple way dealing with EV/non-EV is:
- either not to let vehicles with combustion engine to take EV Enabled parking spaces
- or, let vehicles with combustion engine to park at EV Enabled spaces but apply surcharge.
- or assume that each parking slot is EV-Enabled by default, and only in case of technical fault EV Capability supposed to be turned off manually

### Further improvements:
Currently, system assumes that gates are not letting vehicles in unless another vehicle completely left the parking. For this purpose, our Synchronized List of parking spaces works fine. 
To make system more effective and allow it park incoming vehicles waster we can introduce two Queues: 
- Parking Queue
- Leaving Queue

This will also need some messaging pattern to make Queue Service Observable. As soon as change in any of the queues happened, a queue watcher should start in a separate thread and either let another car park from Parking Queue, or update close Parking Session for a car that has just left the Leaving Queue.

In case if we want implement traditional parking process close to our real world when human interacts with the system, we should accept that might stay unpredictable time in the Parking Queue and that should count into their parking charge. As well as they can try an cheat when leaving, so might want to reject the leaving gate if they were in Leaving Queue longer than some threshold time and make them pay extra.

Additional services could be added:
- Reservation Service (parking on demand)
- Car Washing Service - while car is being washed, it's in a parking queue. If driver decides to leave parking within 5 minutes after their has been washed, they won't be charged for parking.  

![concept](./concept.png)