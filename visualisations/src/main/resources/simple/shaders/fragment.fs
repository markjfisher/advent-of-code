#version 450

in  vec2 outTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec4 colour;
uniform int useColour;

void main()
{
    if ( useColour == 1 )
    {
        fragColor = vec4(colour);
    }
    else
    {
        fragColor = texture(texture_sampler, outTexCoord);
    }
}