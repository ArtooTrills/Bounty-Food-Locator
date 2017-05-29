# Bounty-Location-Tracker

Artoo's Android app helps field agents of lending institutions collect information of their customers at their house or business location. Each field agent is allocated a certain sales zone and they can work within their zone

Build a feature in the app that allows the field agent to transmit his whereabouts during during work hours

## Field agent user flow
**One time set-up:** Save current location to define the zone. You can assume 2km radius from current location

- Start your day (consent to start location tracking)
- Collect basic details about customers (you can skip this)
- Current location is saved in the server every 5 minutes
- End your day (end location tracking)

## Supervisor user flow

- Check the current location of the field agent
- Get notified if the field agent is working outside his allocated zone
- Bonus - see the entire path taken during the course of the day 

## Resources
- Use Firebase for [Push Notifications](https://firebase.google.com/docs/cloud-messaging/android/client) and to [save location data](https://firebase.google.com/docs/database/)
- Use DDMS to simulate location movement

## What are we looking for
- Code structure
- Test cases
- Documentation / Comments

## Final Words

When you are done, just create a pull request on this repo. We would like you to come in and present your code to the whole team.

May the force be with you!
