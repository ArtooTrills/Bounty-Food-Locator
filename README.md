# Bounty-Food-Locator

There are many popular online food delivery services. Delivery agents carry android phones to receive notifcations of pickups and deliveries. Build an agent app that allows the field agent to transmit his whereabouts to the customer

## Delivery agent user flow
- Confirm pick-up (consent to start location tracking)
- Start driving towards the customer location (save location to the server every 5 minutes)
- Confirm delivery (end location tracking)

## Supervisor user flow
- Check the current location of the delivery agent
- Get notified if the delivery is not done 15 mins after pick-up
- Bonus - see all paths taken by the agent during the course of the day 

## Suggested resources
- Use Firebase for [Push Notifications](https://firebase.google.com/docs/cloud-messaging/android/client) and to [save location data](https://firebase.google.com/docs/database/)
- Use DDMS to simulate location movement

## What are we looking for
- Code structure
- Test cases
- Documentation / Comments

## Final Words

When you are done, just create a pull request on this repo. We would like you to come in and present your code to the whole team.

May the force be with you!
