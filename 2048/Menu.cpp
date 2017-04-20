#include <SDL.h>
#include <SDL_image.h>
#include <SDL_ttf.h>
#include <bits/stdc++.h>
#include "Menu.h"

using namespace std;


//The window we'll be rendering to
const int SCREEN_WIDTH = 1280;
const int SCREEN_HEIGHT = 720;
const int lenText = 4;
const int xText = 140;
const int yText = 220;
const std::string link_Play = "imageMenu/Play.png";
const std::string link_Help = "imageMenu/Help.png";
const std::string link_Score = "imageMenu/Score.png";
const std::string link_Play_e = "imageMenu/Play_e.png";
const std::string link_Help_e = "imageMenu/Help_e.png";
const std::string link_Score_e = "imageMenu/Score_e.png";

const std::string link_Header = "imageMenu/header.png";
const std::string link_TutoBG = "imageMenu/TutoBG.png";

const std::string link_Menu = "imageMenu/Menu.png";
const std::string link_Menu_e = "imageMenu/Menu_e.png";
const std::string link_Prev = "imageMenu/Prev.png";
const std::string link_Prev_e = "imageMenu/Prev_e.png";
const std::string link_Next = "imageMenu/Next.png";
const std::string link_Next_e = "imageMenu/Next_e.png";

SDL_Rect Rect[2];
SDL_Rect RectColor[3];
int Widthx,Heightx;
int flag = 0;
int k = 1;
int space = 1;
bool quit;

SDL_Window* mWindow = NULL;
SDL_Renderer* mRenderer = NULL;
TTF_Font *mFont = NULL;
MTexture imageBackground,imageOption[3][2],TextTutoBG,TextTutoReturn,TextTutoMain[lenText][2],imageHeader,imageTutoBG,
                                    imageTutoOpt[3][2],imageSlide[2];


MTexture::MTexture()
{
	//Initialize
	mTexture = NULL;
	mWidth = 0;
	mHeight = 0;
}

MTexture::~MTexture()
{
	//Deallocate
	free();
}

bool MTexture::loadFromFile( std::string path )
{
	//Get rid of preexisting texture
	free();

	//The final texture
	SDL_Texture* newTexture = NULL;

	//Load image at specified path
	SDL_Surface* loadedSurface = IMG_Load( path.c_str() );
	if( loadedSurface == NULL )
	{
		printf( "Unable to load image %s! SDL_image Error: %s\n", path.c_str(), IMG_GetError() );
	}
	else
	{
		//Color key image

		//Create texture from surface pixels
		SDL_SetColorKey(loadedSurface,SDL_TRUE,SDL_MapRGB(loadedSurface->format,0xFF,0xFF,0xFF));
        newTexture = SDL_CreateTextureFromSurface( mRenderer, loadedSurface );
		if( newTexture == NULL )
		{
			printf( "Unable to create texture from %s! SDL Error: %s\n", path.c_str(), SDL_GetError() );
		}
		else
		{
			//Get image dimensions
			mWidth = loadedSurface->w;
			mHeight = loadedSurface->h;
		}

		//Get rid of old loaded surface
		SDL_FreeSurface( loadedSurface );
	}

	//Return success
	mTexture = newTexture;
	return mTexture != NULL;
}

bool MTexture::loadFromRenderedText( std::string textureText, SDL_Color textColor )
{
	//Get rid of preexisting texture
	free();

	//Render text surface
	SDL_Surface* textSurface = TTF_RenderText_Solid( mFont, textureText.c_str(), textColor );
	if( textSurface == NULL )
	{
		printf( "Unable to render text surface! SDL_ttf Error: %s\n", TTF_GetError() );
	}
	else
	{
		//Create texture from surface pixels
        mTexture = SDL_CreateTextureFromSurface( mRenderer, textSurface );
		if( mTexture == NULL )
		{
			printf( "Unable to create texture from rendered text! SDL Error: %s\n", SDL_GetError() );
		}
		else
		{
			//Get image dimensions
			mWidth = textSurface->w;
			mHeight = textSurface->h;
		}

		//Get rid of old surface
		SDL_FreeSurface( textSurface );
	}

	//Return success
	return mTexture != NULL;
}

void MTexture::free()
{
	//Free texture if it exists
	if( mTexture != NULL )
	{
		SDL_DestroyTexture( mTexture );
		mTexture = NULL;
		mWidth = 0;
		mHeight = 0;
	}
}

void MTexture::render( int x, int y ,SDL_Rect* clip)
{
    //Set rendering space and render to screen
    SDL_Rect renderQuad = { x, y, mWidth, mHeight };
    if (clip != NULL)
    {
        renderQuad.w = clip->w;
        renderQuad.h = clip->h;
    }
    SDL_RenderCopy( mRenderer, mTexture, clip, &renderQuad );
}

int MTexture::getWidth()
{
	return mWidth;
}

int MTexture::getHeight()
{
	return mHeight;
}

bool Minit()
{
    bool success = true;
    if (SDL_Init(SDL_INIT_EVERYTHING) < 0)
    {
        printf("SDL could not be created! SDL Error: %s\n",SDL_GetError());
        success = false;
    }
        else
    {
        if( !SDL_SetHint( SDL_HINT_RENDER_SCALE_QUALITY, "1" ) )
		{
			printf( "Warning: Linear texture filtering not enabled!" );
		}

        mWindow = SDL_CreateWindow("Game2048 - Author: Ngo Minh Hoang",SDL_WINDOWPOS_CENTERED,SDL_WINDOWPOS_CENTERED,
                                        SCREEN_WIDTH,SCREEN_HEIGHT,SDL_WINDOW_SHOWN);
        if (mWindow == NULL)
        {
            printf("Window could not be created! SDL Error: %s\n",SDL_GetError());
            success = false;
        }
            else
        {
            mRenderer = SDL_CreateRenderer(mWindow,-1,SDL_RENDERER_ACCELERATED);

            if( mRenderer == NULL )
			{
				printf( "Renderer could not be created! SDL Error: %s\n", SDL_GetError() );
				success = false;
			}
			else
			{
				//Initialize renderer color
				SDL_SetRenderDrawColor( mRenderer, 0xFF, 0xFF, 0xFF, 0xFF );

				//Initialize PNG loading
				int imgFlags = IMG_INIT_PNG;
				if( !( IMG_Init( imgFlags ) & imgFlags ) )
				{
					printf( "SDL_image could not initialize! SDL_image Error: %s\n", IMG_GetError() );
					success = false;
				}

				if( TTF_Init() == -1 )
				{
					printf( "SDL_ttf could not initialize! SDL_ttf Error: %s\n", TTF_GetError() );
					success = false;
				}
			}
        }
    }
    return success;
}

bool loadFont()
{
    bool success = true;
    SDL_Color TextColor = {0,0,0};

    mFont = TTF_OpenFont( "Font/lazy.ttf", 72 );
    if (mFont == NULL) success = false;
    TextTutoBG.loadFromRenderedText("Tutorial",TextColor);

    mFont = TTF_OpenFont( "Font/lazy.ttf", 60 );
    if (mFont == NULL) success = false;
    TextTutoReturn.loadFromRenderedText("Return",TextColor);

    ifstream fi("imageMenu/Tutorial.txt");

    mFont = TTF_OpenFont( "Font/lazy.ttf", 30 );
    if (mFont == NULL) success = false;
    //fi>>lenText;
    for (int i = 0; i < lenText; ++i)
        for (int j = 0; j < 2; ++j)
        {
            std::string s;
            getline(fi,s);
            TextTutoMain[i][j].loadFromRenderedText(s,TextColor);
        }

    return success;
}

bool MloadMedia()
{
    //Loading success flag
    //Load Foo' texture
    if( !imageBackground.loadFromFile( "imageMenu/image.jpg" ) )
    {
        printf( "Failed to load background image!\n" );
        return false;
    }

    if( !imageOption[0][0].loadFromFile( link_Play ) ||!imageOption[0][1].loadFromFile(link_Play_e))
    {
        printf( "Failed to load start image!\n" );
        return false;
    }

    if( !imageOption[1][0].loadFromFile( link_Help ) ||!imageOption[1][1].loadFromFile(link_Help_e) )
    {
        printf( "Failed to load tutorial image!\n" );
        return false;
    }

    if( !imageOption[2][0].loadFromFile( link_Score ) ||!imageOption[2][1].loadFromFile(link_Score_e) )
    {
        printf( "Failed to load highScore image!\n" );
        return false;
    }

    if( !imageTutoOpt[0][0].loadFromFile( link_Menu ) ||!imageTutoOpt[0][1].loadFromFile(link_Menu_e))
    {
        printf( "Failed to load start image!\n" );
        return false;
    }

    if( !imageTutoOpt[1][0].loadFromFile( link_Prev ) ||!imageTutoOpt[1][1].loadFromFile(link_Prev_e))
    {
        printf( "Failed to load start image!\n" );
        return false;
    }

    if( !imageTutoOpt[2][0].loadFromFile( link_Next ) ||!imageTutoOpt[2][1].loadFromFile(link_Next_e))
    {
        printf( "Failed to load start image!\n" );
        return false;
    }

    if (!imageHeader.loadFromFile(link_Header))
    {
        printf( "Failed to load header image!\n" );
        return false;
    }

    if (!imageTutoBG.loadFromFile(link_TutoBG))
    {
        printf( "Failed to load TutoBG image!\n" );
        return false;
    }

    if (!loadFont())
    {
        printf( "Failed to load lazy font! SDL_ttf Error: %s\n", TTF_GetError() );
        return false;
    }


    return true;
}

void Mclose()
{
    //Free loaded images
    imageBackground.free();
    TextTutoBG.free();
    TextTutoReturn.free();
    for (int i=0; i<lenText; ++i)
        for (int j=0; j<2 ; ++j)
            TextTutoMain[i][j].free();

    //Destroy window
    SDL_DestroyRenderer( mRenderer );
    SDL_DestroyWindow( mWindow );
    mWindow = NULL;
    mRenderer = NULL;

    //Quit SDL subsystems
    TTF_CloseFont( mFont );
	mFont = NULL;

    TTF_Quit();
    IMG_Quit();
    SDL_Quit();
}

void drawBackGround()
{
    SDL_SetRenderDrawColor( mRenderer, 0xFF, 0xFF, 0xFF, 0xFF );
    SDL_SetRenderDrawBlendMode(mRenderer,SDL_BLENDMODE_NONE);
    SDL_RenderClear( mRenderer );

    if (Rect[flag].w ==0)
    {
        Rect[flag].x = Widthx;
        flag = 1- flag;
        k = 1;
    }

    Rect[flag].w -= space;
    Rect[1-flag].x -=space; Rect[1-flag].w +=space;
    //Render top left sprite
    imageBackground.render( space*k, 0, &Rect[flag] );

    //Render top right sprite
    imageBackground.render( 0, 0, &Rect[1 - flag] );
    ++k;
}

void Tutorial()
{
    int flagTuto = 2,yTuto = 570,xTuto = 50,spaceTuto = 400,slide = 0,xText = 200,yText= 180;
    bool exit = false;
    while (!exit)
    {
        SDL_Event e;
        if ( SDL_PollEvent( &e ) != 0 )
        {
            if( e.type == SDL_QUIT )
            {
                quit = true;
                return;
            }

            if (e.type == SDL_KEYDOWN)
            {
                if (e.key.keysym.sym == SDLK_LEFT)
                {
                    if (slide == 0) flagTuto = 2-flagTuto ;
                    if (slide == 1 || slide == 2) flagTuto = (flagTuto + 2)%3;
                    if (slide == 3) flagTuto = 1-flagTuto;
                }

                if (e.key.keysym.sym == SDLK_RIGHT)
                {
                    if (slide == 0) flagTuto = 2-flagTuto ;
                    if (slide == 1||slide == 2) flagTuto = (flagTuto + 1)%3;
                    if (slide == 3) flagTuto = 1-flagTuto;
                }

                if (e.key.keysym.sym == SDLK_RETURN)
                {
                    switch (slide)
                    {
                        case 0:
                        {
                            if (flagTuto == 0) return;
                            if (flagTuto == 2)
                            {
                                slide++;
                                flagTuto = 2;
                            }
                            break;
                        }

                        case 3:
                        {
                            if (flagTuto == 0) return;
                            if (flagTuto == 1)
                            {
                                slide--;
                                flagTuto = 1;
                            }
                            break;
                        }

                        case 1:
                        {
                            if (flagTuto == 0) return;
                            if (flagTuto == 2) {++slide;flagTuto = 2;break;}
                            if (flagTuto == 1) {--slide;flagTuto = 2;break;}
                            break;
                        }

                        case 2:
                        {
                            if (flagTuto == 0) return;
                            if (flagTuto == 2) {++slide;flagTuto = 1;break;}
                            if (flagTuto == 1) {--slide;flagTuto = 1;break;}
                            break;
                        }
                    }
                }
            }
        }
        //std::cout<<slide<<' '<<flagTuto<<'\n';
        drawBackGround();
        imageTutoBG.render(65,10);
        switch (slide)
        {
            case 0:
            {
                imageTutoOpt[flagTuto][1].render(xTuto + spaceTuto*flagTuto , yTuto);
                imageTutoOpt[2-flagTuto][0].render(xTuto + spaceTuto*(2-flagTuto) , yTuto);
                break;
            }

            case 3:
            {
                imageTutoOpt[flagTuto][1].render(xTuto + spaceTuto*flagTuto , yTuto);
                imageTutoOpt[1-flagTuto][0].render(xTuto + spaceTuto*(1-flagTuto) , yTuto);
                break;
            }

            default:
            {
                for (int i=0; i<3; ++i)
                {
                    int j=0;
                    if (i == flagTuto) j=1;
                    imageTutoOpt[i][j].render(xTuto +spaceTuto*i , yTuto);
                }
                break;
            }
        }

        TextTutoMain[slide][0].render(xText,yText);
        TextTutoMain[slide][1].render(xText,yText+50);

        SDL_RenderPresent(mRenderer);
        SDL_Delay(3);
        //SDL_Delay(1000);
    }
}


bool Menu()
{
    bool success = false;
	//Start up SDL and create window

	if( !Minit() )
	{
		printf( "Failed to initialize!\n" );
	}
	else
	{
		//Load media
		if( !MloadMedia() )
		{
			printf( "Failed to load media!\n" );
		}
		else
		{
			//Main loop flag
            quit = false;
			bool tuto = false;

			//Event handler
			SDL_Event e;

			Widthx = imageBackground.getWidth();
            Heightx = imageBackground.getHeight();

            RectColor[0] = {450,260,370,100};RectColor[1] = {450,390,370,100};RectColor[2] = {450,520,370,100};
            Rect[0] = {0,0,Widthx,Heightx},Rect[1] = {Widthx,0,0,Heightx};

            int flagColor = 0;
			//While application is running
			while( !quit )
			{
				//Handle events on queue
				while( SDL_PollEvent( &e ) != 0 )
				{
					//User requests quit
					if( e.type == SDL_QUIT )
					{
						quit = true;
					}
                    if (e.type == SDL_KEYDOWN)
                    {
                        if (!tuto)
                        {
                            if (e.key.keysym.sym == SDLK_UP) flagColor = (flagColor + 2)%3;
                            if (e.key.keysym.sym == SDLK_DOWN)  flagColor = (flagColor + 1)%3;
                            if (e.key.keysym.sym == SDLK_RETURN)
                            {
                                if (flagColor % 3 == 0) {quit = true;success = true;}
                                if (flagColor % 3 == 1) Tutorial();
                            }
                        }
                            else
                        {
                            if (e.key.keysym.sym == SDLK_RETURN) tuto = false;
                        }
                    }
				}

				drawBackGround();
                //Render bottom left sprite
                for (int i=0; i<3; ++i)
                {
                    int j=0;
                    if (i == flagColor) j=1;
                    imageOption[i][j].render(450,230+160*i,NULL);
                }
                imageHeader.render(280,10,NULL);

                SDL_RenderPresent(mRenderer);
                SDL_Delay(3);
			}
		}
	}

	//Free resources and close SDL
	Mclose();
	return success;

}
