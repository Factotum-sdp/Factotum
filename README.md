# Factotum

[UI mock of the app](https://www.figma.com/file/eJQUTJq93wlnZxzrw3NjLE/Factotum-UI?node-id=0%3A1&t=zw2E4JaSMoVI2aMI-1)

### Application Design



#### Name :

Factotum



#### Goal :

The main goal of that android application is to plan and record (localization, timing) the route of a courier (bike delivery) during an half-day period called a shift.



#### The feature ideas :

1) Authentication to determine user rights (boss who can see every courier's location and write courier's route plan, or simple courier with writing rights only in his own route plan)
2) Route is planned and edited by the user in a displayed queued list where each line correspond to a destination address

3. Each line is a destination record and will contain :

   - a time-stamp for the arrival time, 
   - an id if the destination it's a direct customer from the courier company. Otherwise, the name if it's an non-direct customer or an ephemeral/occasional destination (which will be a manual entry as, would not be in the database)
   - the task to execute when on place (pick up, delivery, relay, or boss contact)   
   - the address with a link to the default gps app of the phone in order to create a route.
   - other additional details or access to the details from customer database (example : entry code for the door)

4. Localization through GPS sensor for each user, and automatic detection of the arrival in a planned address, to timestamp the destination line.

5. Possibility to see all courier location for the boss (Maybe also for every courier)

6. Upload the log of the GPS tracing and the queued list history when a shift is finished.

7. Possibility for the boss to have a melded queued list with all destination record from all courier, with some filter option. (Problem of consistency with offline mode)

   

#### Requirements :

<u>User support :</u> met by feature 1) 

<u>Split app model :</u> met by storing the customer database and the log of the shift.

<u>Sensor use :</u> GPS would be used

<u>Offline mode :</u> 
by downloading the locally the full or the active customer data from the cloud at the start of a shift, and update the cloud for the log at the end of a shift.
The app can be 100% used off-line, but the boss user will have to be noticed as he can be no longer informed for the location or any queued list update.



#### Fresh Ideas :

1. Font color to differs a manual entry edit from an automated one, and special color for the "boss" user
2. Modify order of the stack dynamically 
3. Notes specific on a line record 
4. Maybe add the manual destination entries of non-direct customer in the database at the end of each shift



#### Notes :
