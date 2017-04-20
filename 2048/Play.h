#ifndef PLAY_H
#define PLAY_H
#include <SDL.h>
#include <SDL_image.h>
#include <SDL_ttf.h>
#include <bits/stdc++.h>

class LTexture
{
	public:
		//Initializes variables
		LTexture();

		//Deallocates memory
		~LTexture();

		//Loads image at specified path
		bool loadFromFile( std::string path );

		bool loadFromRenderedText( std::string textureText, SDL_Color textColor );

		//Deallocates texture
		void free();

		//Renders texture at given point
		void render( int x, int y, SDL_Rect* clip = NULL,
              double angle = 0.0, SDL_Point* center = NULL, SDL_RendererFlip flip = SDL_FLIP_NONE );

		 //Set blending
        void setBlendMode( SDL_BlendMode blending );

        //Set alpha modulation
        void setAlpha( Uint8 alpha );

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

void renderBackground();
int changeX(int i);
int changeY(int i);
void createSquare();
void staySquare();
void createRandomNumber();
int minimizeX(int k,int X);
int minimizeY(int k,int Y);
void reRender();
void writeNumber();
void performAnimation_QuickMove();
void performAnimation_FadedScreen();
bool checktoQuit();
void createRandNum();
void Play();

#endif
