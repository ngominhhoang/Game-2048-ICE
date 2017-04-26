#include "Play.h"
#include "Menu.h"

int main( int argc, char* args[] )
{
    srand(time(NULL));
    while (true)
    {
        //Play(cWindow, cRenderer);
        if (!Menu()) break;
        if (Play()) break;
    }
    //Play();
    //Menu();
	return 0;
}
