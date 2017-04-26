#ifndef MENU_H
#define MENU_H
#include <SDL.h>
#include <SDL_image.h>
#include <bits/stdc++.h>

class MTexture
{
	public:
		//Initializes variables
		MTexture();

		//Deallocates memory
		~MTexture();

		//Loads image at specified path
		bool loadFromFile( std::string path );

        bool loadFromRenderedText( std::string textureText, SDL_Color textColor );
		//Deallocates texture
		void free();

		//Renders texture at given point
		void render( int x, int y, SDL_Rect* clip = NULL);

		//Gets image dimensions
		int getWidth();
		int getHeight();

	private:
		//The actual hardware texture
		SDL_Texture* mTexture;

		//Image dimensions
		int mWidth;
		int mHeight;
};

bool loadFont();
//bool Minit();
bool Menu();
void Tutorial();
void Mclose();
bool MloadMedia();

#endif
