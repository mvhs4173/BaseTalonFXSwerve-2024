# Note Handler's Xbox Controller Button Assignments

Note handler's Xbox controller must be in position 1 on Driver Station's USB tab.

Button assignments are not done yet.
Rotation directions, clockwise or counter-clockwise, are as seen from the robot’s left.

- **Left joystick** - forward rotates main arm (at shoulder joint) counter-clockwise at rather slow fixed speed, raising it; backward rotates it clockwise, lowering it.  Side-to-side does nothing.  This is treated as an on/off trigger, motion past 50% is considered on.
- **Right joystick** - forward rotates shooter (at wrist joint) clockwise at rather slow fixed speed, raising it if arm is down; backward rotates shooter counter-clockwise.  Side-to-side does nothing.  On/off trigger, no speed control.
- **Start button** (small button to right of central Xbox symbol) - set current shoulder and wrist orientations as the zero points. Only do this when the robot is in starting position.  NOT IMPLEMENTED YET
- **A button** - a momentary press starts intake motors, which will run for 5 seconds
- **Left bumper** - move to position for shot to speaker from position very close to front of speaker (arm down to 0 degrees, shooter c. -30).  You must hold down this button until the shot is taken: the arm will move to collection position when you release it.
- **Right bumper** - shoot for speaker, at full power.  Currently you must hold button until shot is done.  This may change to having a momentary press cause a 2 second shoot.
- **Right & left triggers** act like the bumpers but for the amplifier shots.  Both are treated as on/off switches.

Other buttons are not assigned actions now.
