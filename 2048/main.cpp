#include "Play.h"
#include "Menu.h"

int main( int argc, char* args[] )
{
    srand(time(NULL));
    //Play();
    if (Menu()) Play();
	return 0;
}
