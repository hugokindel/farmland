<div align="center">
  <img width="500px" src="https://uni-farmland.s3.eu-west-3.amazonaws.com/farmland.png" style="image-rendering: pixelated; image-rendering: -moz-crisp-edges; image-rendering: crisp-edges;">
</div>

# Farmland

Farmland is a management game where you need to take care of your farm while being careful about the economy to stay in business while your competitors will try to get on top of you!

## Screenshots

<div align="center">
    <img src="https://uni-farmland.s3.eu-west-3.amazonaws.com/screenshot1.png" height="180px" style="image-rendering: pixelated; image-rendering: -moz-crisp-edges; image-rendering: crisp-edges;">
    <img src="https://uni-farmland.s3.eu-west-3.amazonaws.com/screenshot2.png" height="180px" style="image-rendering: pixelated; image-rendering: -moz-crisp-edges; image-rendering: crisp-edges;">
    <img src="https://uni-farmland.s3.eu-west-3.amazonaws.com/screenshot3.png" height="180px" style="image-rendering: pixelated; image-rendering: -moz-crisp-edges; image-rendering: crisp-edges;">
</div>

## Features

- A homemade game engine.
- A turn by turn based farm management gameplay.
- A singleplayer and multiplayer experience.
- Useful debugging tools and console system.
- An easy to extend codebase (add an item, add a language, etc.).

To see more details about the features included and the project architecture, please take a look [here](FEATURES.md).

## How to use

### From sources

First, you need to clone the repository by using `git clone git@gaufre.informatique.univ-paris-diderot.fr:hugokindel/farmland.git` or `git clone https://gaufre.informatique.univ-paris-diderot.fr/hugokindel/farmland.git`

#### Compile with an IDE (if it supports Gradle):

1) Open the project's directory in your IDE.
2) Use the `run` gradle task.

#### Compile with the command line:

1) Open a terminal in your project directory.
2) Run gradle wrapper (it will download all dependencies, including gradle itself) `./gradlew build`.
3) You can finally run the project with `./gradlew run`.

### From a binary distribution

If you got a binary release (containing all the libs, assets and the bin directory), you can directly use `./farmland` (on UNIX systems) or `./farmland.bat` (on Windows).

## Try the project

Instructions to try every interesting features included within this project can be found [here](INSTRUCTIONS.md).

## Roadmap

The roadmap can be read [here](ROADMAP.md).

## Third party libraries

- [LWJGL](https://www.glfw.org/)  
  Gives access to other low level libraries (GLFW, OpenGL, OpenAL).
- [GLFW](https://www.lwjgl.org/)  
  Handles the window and the input.
- [OpenGL](https://www.opengl.org/)  
  Creates the rendering context, and a way to communicate with the GPU.
- [OpenAL](https://www.openal.org/)  
  Handles the audio system.
- [ImGui](https://github.com/ocornut/imgui) (with [imgui-java](https://github.com/SpaiR/imgui-java) as a binding)  
  Permits to rapidly develop usable GUI in a game.

## Contributors

- [LE CORRE Léo](https://gaufre.informatique.univ-paris-diderot.fr/lecorre)
- [KINDEL Hugo](https://gaufre.informatique.univ-paris-diderot.fr/hugokindel)
- [PAULAS VICTOR Francis](https://gaufre.informatique.univ-paris-diderot.fr/paulasvi)
- [JAUROYON Maxime](https://gaufre.informatique.univ-paris-diderot.fr/jauroyon)

## License

This project is made for educational purposes only and any part of it can be used freely.