# BYOW Design Document

**Project Partner Names**: 
<p>Anastasia Ilina<br>
Arely Badillo Alvarado</p>

## Classes and Data Structures

### Main Class

#### Instance Variables

1. Width, width of the world
2. Height, height of the world
3. Random Variable, rand


### Main Menu

#### Instance Variables

1. Width, main menu / world
2. Height, main menu / world
3. Main Menu screen color

### Rooms

### Instance Variables

1. CenterX
2. CenterY
3. height, room
4. width, room
5. rooms, an array list of rooms 
6. Random Variable, rand

## Algorithms
1. main constructor:
<p>Sets up the menu. After having the user <br> </p>

2. createWorld method
<p>The createWorld method focuses on setting up the rooms,<br>
connecting the rooms with hallways, and building the walls<br>
around the entire building. It should call the<br>
emptySpaceProportion method so that it checks that there<br>
isn't more than 50% empty space in our world.</p>

3. emptySpaceProportion method
<p>Counts how many Nothing Tiles there are and divides it<br>
by the area of the window (height * width).</p>

### Main Class

### Main Menu
### Rooms

## Persistence

