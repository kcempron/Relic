# Relic
Project Relic is a facebook messenger bot that reacts to daily life activities.

## Commands
Relic is capable of engaging in certain conversations by providing it with a message that starts with a specificed command tag followed by a `:`.
> USER: "mood: I'm feeling good today!"

> RELIC: "You're trying to send a mood message."
### Usable Commands
Currently, the following commands are recognizable:
- "new user" **FUNCTIONAL**
```
Creates a new user profile that Relic can use to store personal information if an account does not exist.
NOTE: Relic will not engage with any new users until this command has been called.
```
- "reset user" 
```
If a user profile exists, reset the user data.
```
- "mood"
```
Records a user's mood at that given moment.
```
- "thought"
```
Records a user's thought at that given moment.
```
- "gym"
```
Returns a list of workout routines for that given day.
```
## Future Work
I'm looking to add in a budgeting feature which can monitor ones regular expenditure and report week-over-week spending.
