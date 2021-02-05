 in vec3 vertex;
    //vertex shader.
    void main() // write a main and output should be done using special variables
    {
      // output using special variables.
    gl_Position = vec4(vertex.xyz, 1.0);
    }