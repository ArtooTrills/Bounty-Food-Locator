# Bounty-Food-Locator

There are many popular online food delivery services. The promise is to deliver within 20 minutes once the food has been picked up from the restaurant. Build an app that allows the delivery agent to transmit his whereabouts to the customer

## Delivery agent user flow
- Confirm pick-up (consent to start location tracking)
- Start driving towards the customer location (save location to the server every 5 minutes)
- Confirm delivery (end location tracking)

## Customer user flow
- Check the current location of the delivery agent
- Get notified if the delivery is delayed even after pick-up
- Bonus: see all paths taken by the agent during the course of the day (useful for the agent's manager)

## Suggested resources
- Use Firebase for [Push Notifications](https://firebase.google.com/docs/cloud-messaging/android/client) and to [save location data](https://firebase.google.com/docs/database/)
- Use DDMS to simulate location movement

## What are we looking for
- Code structure
- Test cases
- Android Studio tools
- Documentation / Comments

## Final Words

When you are done, just create a pull request on this repo. We would like you to come in and present your code to the whole team.

May the force be with you!
