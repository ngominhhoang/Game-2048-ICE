#include <SDL.h>
#include <SDL_image.h>
#include <SDL_ttf.h>
#include <SDL_mixer.h>
#include <bits/stdc++.h>
#include "Play.h"

using namespace std;

const int SCREEN_WIDTH = 1280;
const int SCREEN_HEIGHT = 720;
const int limitSecond = 5;
const int sum_gImage = 14;
const int sum_randImage = 3;
const int sum_letter = 8;
const int sum_rank = 5;
const int sum_GPMusic = 4;
const int sum_GPSoundEffect = 6;
const std::string link_gImage[21] = {"Image/2.png","Image/4.png","Image/8.png","Image/16.png","Image/32.png","Image/64.png",
    "Image/128.png","Image/256.png","Image/512.png","Image/1024.png","Image/2048.png","Image/4096.png","Image/8192.png","Image/16384.png"};

const std::string link_gImage_Rotation[14] = {"Image/2_ro.png","Image/4_ro.png","Image/8_ro.png","Image/16_ro.png","Image/32_ro.png","Image/64_ro.png",
    "Image/128_ro.png","Image/256_ro.png","Image/512_ro.png","Image/1024_ro.png","Image/2048_ro.png","Image/4096_ro.png","Image/8192_ro.png","Image/16384_ro.png"};

const std::string link_letter[8] = {"Image/G.png","Image/A.png","Image/M.png","Image/E.png","Image/O.png","Image/V.png","Image/E2.png","Image/R.png"};

const std::string link_BG = "Image/Background.jpg";
const std::string link_NumBG = "Image/NumBG.png";
const std::string link_Empty = "Image/empty.png";
const std::string link_Ice[3] = {"Image/ice3.png","Image/ice2.png","Image/ice1.png"};
const std::string link_ICE = "Image/ice.png";
const std::string link_Random[3] = {"Image/Rand3.png","Image/Rand2.png","Image/Rand1.png"};
const std::string link_PowerBox = "Image/powerBox.png";
const std::string link_Liquid = "Image/Liquid.png";
const std::string link_Mirror = "Image/Mirror.png";
const std::string link_PlayAgain = "Image/PlayAgain.png";
const std::string link_BacktoMenu = "Image/BacktoMenu.png";
const std::string link_PlayAgain_e = "Image/PlayAgain_e.png";
const std::string link_BacktoMenu_e = "Image/BacktoMenu_e.png";
const std::string link_CongratBoard = "Image/CongratulationBoard.png";
const std::string link_Star[sum_rank] = {"Image/Rank1.png","Image/Rank2.png","Image/Rank3.png","Image/Rank4.png","Image/Rank5.png"};
const std::string link_GPMusic[sum_GPMusic] = {"Music/bensound-dance.mp3","Music/Bumbling-Burglars.mp3","Music/Disco-Ants-Go-Clubbin.mp3",
                                                            "Music/alun2-endcredits.mp3"};

const std::string link_GPSoundEffect[sum_GPSoundEffect] = {"Music/2516__jonnay__dropsine.wav","Music/172667__underlineddesigns__military-rotor-loop.wav",
                                                "Music/65917__bristolstories__tinkle-smash-glass.wav","Music/345911__abstractasylum__1-9-massive-freeze.wav",
                                                "Music/73750__timbre__remix-of-benboncan-sad-trombone-more-wah-bright-de-clicked.wav",
                                                "Music/245639__nickrave__moreclaps.aiff"};

const int row[4] = {0,0,-1,1};
const int column[4] = {1,-1,0,0};
const int incPoint[14] = {6,6,6,4,4,4,2,2,2,2,2,2,2,2};
const int decPoint[14] = {10,10,10,16,16,16,20,20,20,24,24,24,24,24};
const int DefaultScore[14] = {20,50,100,150,250,350,500,700,1000,1500,2000,3000,4500,10000};
const int BonusScore[6] = {1,2,2,3,4,5};
const int fix_x = 100;
const int fix_y = -20;
bool randNum[4][4],appear,merged,iceNum[4][4];
bool endgame , exitGame;
int tryAgain ;
int powerPoint,combo,score,volumn;

struct Format
{
    int xStart;
    int yStart;
    int xAdd;
    int yAdd;
    int Space;
    int OldNumber;
    int PresentNumber;
};

struct Winner
{
    int Score;
    string Name;
};

Mix_Music *GPMusic[sum_GPMusic] = {NULL,NULL,NULL,NULL};
Mix_Chunk *GPSoundEffect[sum_GPSoundEffect] = {NULL,NULL,NULL,NULL,NULL,NULL};

Format Square[4][4];

Winner gRank[5];

//Texture wrapper class

//Starts up SDL and creates window

SDL_Window* gWindow = NULL;

//The window renderer
SDL_Renderer* gRenderer = NULL;

TTF_Font* gFont = NULL;

//The window we'll be rendering to

LTexture gImage[sum_gImage],randImage[sum_randImage],background,NumBG,Empty,iceImage[sum_randImage],ICE,ICE2,gImage_Rotation[sum_gImage],gLetter[sum_letter];
LTexture powerBox , liquid, mirror, TextScore, PlayAgain[2], BacktoMenu[2],CongratBoard,rankStar[sum_rank];


LTexture::LTexture()
{
	//Initialize
	mTexture = NULL;
	mWidth = 0;
	mHeight = 0;
}

LTexture::~LTexture()
{
	//Deallocate
	free();
}

bool LTexture::loadFromFile( std::string path )
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
        newTexture = SDL_CreateTextureFromSurface( gRenderer, loadedSurface );
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

bool LTexture::loadFromRenderedText( std::string textureText, SDL_Color textColor )
{
	//Get rid of preexisting texture
	free();

	//Render text surface
	SDL_Surface* textSurface = TTF_RenderText_Solid( gFont, textureText.c_str(), textColor );
	if( textSurface == NULL )
	{
		printf( "Unable to render text surface! SDL_ttf Error: %s\n", TTF_GetError() );
	}
	else
	{
		//Create texture from surface pixels
        mTexture = SDL_CreateTextureFromSurface( gRenderer, textSurface );
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

void LTexture::setBlendMode( SDL_BlendMode blending )
{
    //Set blending function
    SDL_SetTextureBlendMode( mTexture, blending );
}

void LTexture::setAlpha( Uint8 alpha )
{
    //Modulate texture alpha
    SDL_SetTextureAlphaMod( mTexture, alpha );
}

void LTexture::free()
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

void LTexture::render( int x, int y, SDL_Rect* clip,
                      double angle, SDL_Point* center, SDL_RendererFlip flip )
{
	//Set rendering space and render to screen
	SDL_Rect renderQuad = { x, y, mWidth, mHeight };

	//Set clip rendering dimensions
	if( clip != NULL )
	{
		renderQuad.w = clip->w;
		renderQuad.h = clip->h;
	}

	//Render to screen
	SDL_RenderCopyEx( gRenderer, mTexture, clip, &renderQuad, angle, center, flip );
}

int LTexture::getWidth()
{
	return mWidth;
}

int LTexture::getHeight()
{
	return mHeight;
}

bool init()
{
	//Initialization flag
	bool success = true;

	//Initialize SDL
	if( SDL_Init( SDL_INIT_VIDEO ) < 0 )
	{
		printf( "SDL could not initialize! SDL Error: %s\n", SDL_GetError() );
		success = false;
	}
	else
	{
		//Set texture filtering to linear
		if( !SDL_SetHint( SDL_HINT_RENDER_SCALE_QUALITY, "1" ) )
		{
			printf( "Warning: Linear texture filtering not enabled!" );
		}

		//Create window
		gWindow = SDL_CreateWindow( "Game2048 - Author: Ngo Minh Hoang", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, SCREEN_WIDTH, SCREEN_HEIGHT, SDL_WINDOW_SHOWN );
		if( gWindow == NULL )
		{
			printf( "Window could not be created! SDL Error: %s\n", SDL_GetError() );
			success = false;
		}
		else
		{
			//Create vsynced renderer for window
			gRenderer = SDL_CreateRenderer( gWindow, -1, SDL_RENDERER_ACCELERATED | SDL_RENDERER_PRESENTVSYNC );
			if( gRenderer == NULL )
			{
				printf( "Renderer could not be created! SDL Error: %s\n", SDL_GetError() );
				success = false;
			}
			else
			{
				//Initialize renderer color
				SDL_SetRenderDrawColor( gRenderer, 0xFF, 0xFF, 0xFF, 0xFF );

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

				if( Mix_OpenAudio( 44100, MIX_DEFAULT_FORMAT, 2, 2048 ) < 0 )
                {
                    printf( "SDL_mixer could not initialize! SDL_mixer Error: %s\n", Mix_GetError() );
                    success = false;
                }
			}
		}
	}

	return success;
}

bool loadImage(LTexture* gImageX,std::string linkX)
{
	//Loading success flag
	bool success = true;

	//Load sprite sheet texture
	if( !gImageX -> loadFromFile( linkX ) )
	{
		printf( "Failed to load walking animation texture!\n" );
		success = false;
	}

	return success;
}

bool loadMedia()
{
    if (! loadImage(&background,link_BG))
    {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
    }

    if (! loadImage(&NumBG,link_NumBG))
    {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
    }

    if (! loadImage(&Empty,link_Empty))
    {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
    }

    if (! loadImage(&ICE,link_ICE)||! loadImage(&ICE2,link_ICE))
    {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
    }
    ICE.setBlendMode(SDL_BLENDMODE_BLEND);

    if (! loadImage(&powerBox,link_PowerBox))
    {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
    }

    if (! loadImage(&liquid,link_Liquid))
    {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
    }

    if (! loadImage(&mirror,link_Mirror))
    {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
    }

    if (! loadImage(&PlayAgain[0],link_PlayAgain) || ! loadImage(&PlayAgain[1],link_PlayAgain_e))
    {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
    }

    if (! loadImage(&BacktoMenu[0],link_BacktoMenu) || ! loadImage(&BacktoMenu[1],link_BacktoMenu_e))
    {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
    }

    for (int i=0; i<sum_letter; ++i)
    {
        if (! loadImage(&gLetter[i],link_letter[i]))
        {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
        }
        gLetter[i].setBlendMode(SDL_BLENDMODE_BLEND);
    }

    for (int i=0; i<sum_gImage; ++i)
    {
        if (! loadImage(&gImage[i],link_gImage[i]))
        {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
        }
        gImage[i].setBlendMode(SDL_BLENDMODE_BLEND);
    }

    for (int i=0; i<sum_gImage; ++i)
    {
        if (! loadImage(&gImage_Rotation[i],link_gImage_Rotation[i]))
        {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
        }
        gImage[i].setBlendMode(SDL_BLENDMODE_BLEND);
    }

    for (int i=0; i<sum_randImage; ++i)
    {
        if (! loadImage(&randImage[i],link_Random[i]))
        {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
        }

        if (! loadImage(&iceImage[i],link_Ice[i]))
        {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
        }
    }

    gFont = TTF_OpenFont( "Font/tahomabd.ttf", 60 );
    if (gFont == NULL) return false;

    ifstream file_Score("Text/Score.txt");
    ifstream file_Name("Text/Name.txt");
    for (int i=0; i<sum_rank; ++i)
    {
        file_Score >> gRank[i].Score;
        getline(file_Name , gRank[i].Name);

        if (! loadImage(&rankStar[i],link_Star[i]))
        {
            printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
            return false;
        }
    }
    if (! loadImage(&CongratBoard,link_CongratBoard))
    {
        printf("Unable to load Image! SDL Error: %s\n",SDL_GetError());
        return false;
    }

    for (int i=0; i<sum_GPMusic; ++i)
    {
        GPMusic[i] = Mix_LoadMUS( link_GPMusic[i].c_str() );
        if( GPMusic[i] == NULL )
        {
            printf( "Failed to load beat music! SDL_mixer Error: %s\n", Mix_GetError() );
            return false;
        }
    }

    for (int i=0; i<sum_GPSoundEffect; ++i)
    {
        GPSoundEffect[i] = Mix_LoadWAV( link_GPSoundEffect[i].c_str() );
        if( GPSoundEffect[i] == NULL )
        {
            printf( "Failed to load beat music! SDL_mixer Error: %s\n", Mix_GetError() );
            return false;
        }
    }

    return true;
}

void close()
{
    Mix_HaltMusic();
	for (int i=0; i<sum_gImage; ++i)
    {
        gImage_Rotation[i].free();
        gImage[i].free();
    }

    for (int i=0; i<sum_randImage; ++i)
    {
        randImage[i].free();
        iceImage[i].free();
    }

    for (int i=0; i<sum_letter; ++i)
    {
        gLetter[i].free();
    }

    for (int i=0; i<sum_rank; ++i)
    {
        rankStar[i].free();
    }

    background.free();
    Empty.free();
    NumBG.free();
    ICE.free();
    ICE2.free();
    powerBox.free();
    liquid.free();
    mirror.free();
    CongratBoard.free();

    for (int i=0; i<2; ++i)
    {
        PlayAgain[i].free();BacktoMenu[i].free();
    }

    for (int i=0; i<sum_GPMusic; ++i)
    {
        Mix_FreeMusic(GPMusic[i]);
        GPMusic[i] = NULL;
    }

    for (int i=0; i<sum_GPSoundEffect; ++i)
    {
        Mix_FreeChunk(GPSoundEffect[i]);
        GPSoundEffect[i] = NULL;
    }


	//Destroy window
	SDL_DestroyRenderer( gRenderer );
	SDL_DestroyWindow( gWindow );
    TTF_CloseFont( gFont );

	gWindow = NULL;
	gRenderer = NULL;
	gFont = NULL;


	//Quit SDL subsystems
	IMG_Quit();
	//SDL_Quit();
	TTF_Quit();
}

int changeX(int i)
{
    return 190+110*i ;
}

int changeY(int i)
{
    return 210+110*i;
}

int transferScore(int X)
{
    return DefaultScore[X];
}

int transferCombo(int X)
{
    return BonusScore[X];
}

int minimizeX(int k,int X)
{
    return X + 10*(k+1) ;
}

int minimizeY(int k,int Y)
{
    return Y + 10*(k+1);
}

Format ReFormatSquare(int k_xStart, int k_yStart, int k_xAdd, int k_yAdd, int k_Space, int k_OldNum, int k_PresentNum)
{
    Format point;

    point.xStart = changeX(k_xStart);
    point.yStart = changeY(k_yStart);
    point.xAdd = k_xAdd;
    point.yAdd = k_yAdd;
    point.Space = k_Space;
    point.OldNumber = k_OldNum;
    point.PresentNumber = k_PresentNum;

    return point;
}

void createIceNum()
{
    for (int i=0; i<4; ++i)
        for (int j=0; j<4; ++j)
            iceNum[i][j] = false;
}

void createSquare()
{
    for (int i=0; i<4; ++i)
        for (int j=0; j<4; ++j)
        {
            Square[i][j] = ReFormatSquare(i, j, 0, 0, 0, -1, -1);
        }
}

void createRandNum()
{
    for (int i=0; i<4; ++i)
        for (int j=0; j<4; ++j)
            randNum[i][j] = false;
}

void createRandomNumber()
{
    int Count = 0;
    for (int i = 0; i < 4; ++i)
        for (int j=0; j < 4; ++j)
            if (Square[i][j].PresentNumber == -1) ++Count;
    Count = rand()%Count;

    for (int i = 0; i < 4; ++i)
        for (int j = 0; j < 4; ++j)
        {
            if (Square[i][j].PresentNumber != -1 ) continue;
            --Count;
            if (Count < 0 )
            {
                randNum[i][j] = true;
                int Num;
                if (rand()%6 == 1) Num =1; else Num = 0;
                Square[i][j] = ReFormatSquare(i, j, 0, 0, 0, Num, Num);
                return;
            }
        }

}

void creategImageBlendingMode()
{
    for (int i=0; i<sum_gImage; ++i)
        gImage[i].setAlpha(255);
    ICE.setAlpha(255);
}

void drawScore()
{
    std::string s = "",s_max = "";
    int ss2 = score;
    while (ss2!=0)
    {
        char x;
        x =  (ss2%10)+48;
        s = x+s;
        ss2/=10;
    }
    if (score == 0) s = "0";

    ss2 = gRank[0].Score;
    while (ss2!=0)
    {
        char x;
        x =  (ss2%10)+48;
        s_max = x+s_max;
        ss2/=10;
    }
    if (gRank[0].Score == 0) s_max = "0";
    if (score > gRank[0].Score) s_max = s;

    int add = (s.length()-1)*18;
    SDL_Color TextColor = {0,0,0};
    TextScore.loadFromRenderedText(s,TextColor);
    TextScore.render(1075-add,215);

    add = (s_max.length()-1)*18;
    TextScore.loadFromRenderedText(s_max,TextColor);
    TextScore.render(1075-add,507);
}

void drawPowerBox(int PPoint)
{
    SDL_Rect PPRect;
    int hh;
    hh = PPoint*4;
    PPRect.x = 0;
    PPRect.y = 400 - hh;
    PPRect.w = 109;
    PPRect.h = hh;
    //SDL_SetRenderDrawColor( gRenderer, 0, 255, 0, 255 );
    powerBox.render(750,100);
    liquid.render(790,610 - hh,&PPRect);
    mirror.render(790,202);
    drawScore();
}

void renderSquare()
{
    for (int i = 0; i < 4; ++i)
        for (int j = 0; j < 4; ++j)
        {
            int Num;
            Num = Square[i][j].PresentNumber;
            //if (tryAgain>1)cout<<Num<<endl;

            if (Num == -1 || endgame)
            {
                Empty.render(changeX(i),changeY(j),NULL);
                if (Num == -1) continue;
            }

            if (!endgame) gImage[Num].render(changeX(i),changeY(j));
                else gLetter[Num].render(changeX(i),changeY(j));

            if (iceNum[i][j]) ICE.render(changeX(i),changeY(j));
        }
}

void renderBackground()
{
    //cout<<volumn<<endl;
    Mix_VolumeMusic(volumn);
    if (Mix_PlayingMusic() == 0)
    {
        //cout<<1;
        int t = rand()%3;
        //if (endgame) t = 3;
        //cout<<t;
        //t=2;
        if (!endgame) Mix_PlayMusic(GPMusic[t],2);
        switch (t)
        {
            case 0:
                volumn = 25;break;
            case 1:
                volumn = 100;break;
            case 2:
                volumn = 100;break;
            case 3:
                volumn = 100;break;
        }
    }
    SDL_SetRenderDrawColor( gRenderer, 0xFF, 0xFF, 0xFF, 0xFF );
    SDL_RenderClear( gRenderer );
    background.render(0,0,NULL);
    NumBG.render(280 - fix_x,180-fix_y,NULL);
}

void staySquare()
{
    for (int i=0; i<4; ++i)
        for (int j=0; j<4; ++j)
        {
            int PNum = Square[i][j].PresentNumber;
            Square[i][j] = ReFormatSquare(i, j, 0, 0, 0, PNum, PNum);
            //if (PNum != -1) std::cout << Square[i][j].PresentNumber;
        }
}

void performAnimation_AppearRandomSquare()
{
    for (int k = 2; k >=0; --k)
    {
        renderBackground();
        drawPowerBox(powerPoint);
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
            {
                int Num;
                Num = Square[i][j].PresentNumber;

                if (Num == -1)
                {
                    Empty.render(changeX(i),changeY(j),NULL);
                    continue;
                }

                if (randNum[i][j])
                {
                    randImage[k].render(minimizeX(k,changeX(i)),minimizeY(k,changeY(j)),NULL);
                    continue;
                }
                gImage[Num].render(changeX(i),changeY(j));

                if (iceNum[i][j]) ICE.render(changeX(i),changeY(j),NULL);
            }
        //if (lim!=1) SDL_Delay(1000);
        SDL_RenderPresent(gRenderer);
        SDL_Delay(15);
    }

}

void performAnimation_QuickMove()
{

    for (int k = 0; k<limitSecond; ++k)
    {
        renderBackground();
        drawPowerBox(powerPoint);

        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
            {
                //if (Square[i][j].PresentNumber == -1)
               // {
                    Empty.render(changeX(i),changeY(j),NULL);
                   // continue;
               // }
            }

        for (int i=0; i<4; ++i)
            for (int j=0; j<4; ++j)
            {
                //std::cout<<j<<' '<<Ani[j].xStart<<' '<<Ani[j].yStart<<' '<<(Ani[j].yAdd*Ani[j].Space)<<' '<<Ani[j].Space<<'\n';
                int oldNum = Square[i][j].OldNumber;
                if (oldNum == -1) continue;

                gImage[oldNum].render( Square[i][j].xStart, Square[i][j].yStart , NULL);
                if (iceNum[i][j]) ICE.render(changeX(i),changeY(j));

                Square[i][j].xStart += (Square[i][j].xAdd * Square[i][j].Space);
                Square[i][j].yStart += (Square[i][j].yAdd * Square[i][j].Space);
            }

        //SDL_Delay(2000);
        SDL_RenderPresent(gRenderer);
    }
}

void performAnimation_FadedScreen() // need to fix
{
    for (int a=100; a>=0; a-=1)
    {
        //background.setAlpha(a);
        //NumBG.setAlpha(a);
        for (int i=0; i<sum_gImage; ++i)
            gImage[i].setAlpha(a);
        ICE.setAlpha(a);
        renderBackground();
        drawPowerBox(powerPoint);
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
            {
                int Num;
                Num = Square[i][j].PresentNumber;
                Empty.render(changeX(i),changeY(j));
                gImage[Num].render(changeX(i),changeY(j));
                if (iceNum[i][j]) ICE.render(changeX(i),changeY(j));
            }
        SDL_RenderPresent(gRenderer);
        SDL_Delay(17);
    }
}

void performAnimation_ChangePowerBox(int start, int target)
{
    SDL_Event e;
    while (SDL_PollEvent(&e) != 0) {};
    int add;
    if (start>target) add = -1; else add = 1;
    for (int i=start; i!=target; i+=add)
    {
        renderBackground();
        renderSquare();
        drawPowerBox(i);
        SDL_RenderPresent( gRenderer );
    }
}

void performAnimation_FreezingSquare()
{
    Mix_PlayChannel( -1, GPSoundEffect[3], 0 );

    int Count = 0,iceNow_x = -1,iceNow_y = -1;

    for (int i = 0; i < 4; ++i)
        for (int j=0; j < 4; ++j)
            if (Square[i][j].PresentNumber != -1 && !iceNum[i][j]) ++Count;
    //std::cout<<Count<<' '<<iceNow_x<<' '<<iceNow_y<<'\n';
    Count = rand()%Count;

    for (int i = 0; i < 4; ++i)
        for (int j = 0; j < 4; ++j)
        {
            if (iceNow_x != -1) break;
            if (Square[i][j].PresentNumber == -1 || iceNum[i][j]) continue;
            --Count;
            if (Count < 0 )
            {
                iceNum[i][j] = true;
                iceNow_x = i;
                iceNow_y = j;
            }
        }
    //std::cout<<Count<<' '<<iceNow_x<<' '<<iceNow_y;

    for (int k = 2; k >=0; --k)
    {
        renderBackground();
        drawPowerBox(powerPoint);
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
            {
                int Num;
                Num = Square[i][j].PresentNumber;

                if (Num == -1)
                {
                    Empty.render(changeX(i),changeY(j),NULL);
                    continue;
                }

                gImage[Num].render(changeX(i),changeY(j));
                if (i == iceNow_x && j == iceNow_y)
                {
                    iceImage[k].render(minimizeX(k,changeX(i)),minimizeY(k,changeY(j)),NULL);
                    continue;
                }
                if (iceNum[i][j]) ICE.render(changeX(i),changeY(j));
            }
        //if (lim!=1) SDL_Delay(1000);
        SDL_RenderPresent(gRenderer);
        SDL_Delay(15);
    }
}

void performAnimation_BreakIce(int ii,int jj)
{
    iceNum[ii][jj] = false;
    int x_start = changeX(ii) , y_start = changeY(jj);

    SDL_Rect Rect1 = {0,0,50,50}, Rect2 = {50,0,50,50},
        Rect3 = {0,50,50,50}, Rect4 = {50,50,50,50};

    int alpha = 100;
    SDL_Point P1 = {x_start,y_start},
              P2 = {x_start+50,y_start} ,
              P3 = {x_start,y_start+50} ,
              P4 = {x_start+50,y_start+50} ;

    double angle = 0.0;
    for (int k=0; k<20; ++k)
    {
        renderBackground();
        renderSquare();
        drawPowerBox(powerPoint);

        ICE2.render(P1.x,P1.y,&Rect1,angle,NULL,SDL_FLIP_NONE);
        ICE2.render(P2.x,P2.y,&Rect2,angle,NULL,SDL_FLIP_NONE);
        ICE2.render(P3.x,P3.y,&Rect3,angle,NULL,SDL_FLIP_NONE);
        ICE2.render(P4.x,P4.y,&Rect4,angle,NULL,SDL_FLIP_NONE);

        ICE2.setAlpha(alpha);
        SDL_RenderPresent( gRenderer );

        P1.x -= 10; P1.y -= 10;
        P2.x += 10; P2.y -= 10;
        P3.x -= 10; P3.y += 10;
        P4.x += 10; P4.y += 10;
        angle = angle + 30;
        alpha = alpha - 5;

        SDL_Delay(50);
    }
    ICE2.setAlpha(100);
}

void performAnimation_x2Square()
{

    bool kt =false;
    for (int i=0; i<4; ++i)
        for (int j=0; j<4; ++j)
            if (iceNum[i][j])
            {
                kt = true;
                Mix_PlayChannel( -1, GPSoundEffect[2], 0 );
                performAnimation_BreakIce(i,j);
            }
    if (kt) return;

    Mix_PlayChannel( -1, GPSoundEffect[1], 0 );

    int maxx = -1,x2Now_x = -1,x2Now_y = -1;

    for (int i = 0; i < 4; ++i)
        for (int j=0; j < 4; ++j)
            if (Square[i][j].PresentNumber > maxx)
            {
                maxx = Square[i][j].PresentNumber;
                x2Now_x = i;
                x2Now_y = j;
            }

    Square[x2Now_x][x2Now_y].PresentNumber++;
    Square[x2Now_x][x2Now_y].OldNumber = Square[x2Now_x][x2Now_y].PresentNumber;


    SDL_Rect x2Rect;
    x2Rect.x = 0; x2Rect.y = 0; x2Rect.w = 100; x2Rect.h = 100;

    int x = changeX(x2Now_x) , y = changeY(x2Now_y) , flag=0 , add = 14 , x2Num = Square[x2Now_x][x2Now_y].PresentNumber;

    while( abs(add)<=20 )
    {
        flag+=add;
        x2Rect.w-=2*add;x2Rect.x+=add;
        if (flag>=50) add = -add;
        if (flag<=0)
        {
            add = -add;
            add = add+2;
        }

        SDL_SetRenderDrawColor( gRenderer, 0xFF, 0xFF, 0xFF, 0xFF );
        SDL_RenderClear( gRenderer );

        renderBackground();
        drawPowerBox(powerPoint);

        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
            {
                int Num;
                Num = Square[i][j].PresentNumber;

                if (i == x2Now_x && j == x2Now_y) continue;
                if (Num == -1)
                {
                    Empty.render(changeX(i),changeY(j),NULL);
                    continue;
                }

                gImage[Num].render(changeX(i),changeY(j));
                if (iceNum[i][j]) ICE.render(changeX(i),changeY(j));
            }

            gImage_Rotation[x2Num-1].render(x+flag,y,&x2Rect);
            SDL_RenderPresent( gRenderer );
            //SDL_Delay(50);
    }

    add = 20;
    while( abs(add) >=14 )
    {
        //std::cout<<1<<' '<<add<<'\n';
        flag+=add;
        x2Rect.w-=2*add;x2Rect.x+=add;

        if (flag>=50) add = -add;
        if (flag<=0)
        {
            add = -add;
            add = add-2;
        }

        SDL_SetRenderDrawColor( gRenderer, 0xFF, 0xFF, 0xFF, 0xFF );
        SDL_RenderClear( gRenderer );

        renderBackground();
        drawPowerBox(powerPoint);

        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
            {
                int Num;
                Num = Square[i][j].PresentNumber;

                if (i == x2Now_x && j == x2Now_y) continue;
                if (Num == -1)
                {
                    Empty.render(changeX(i),changeY(j),NULL);
                    continue;
                }

                gImage[Num].render(changeX(i),changeY(j));
                if (iceNum[i][j]) ICE.render(changeX(i),changeY(j));
            }

        gImage_Rotation[x2Num].render(x+flag,y,&x2Rect);
        SDL_RenderPresent( gRenderer );
        //SDL_Delay(1);
    }
}

void performAnimation_dropGAMEOVER()
{
    Mix_PlayChannel( -1, GPSoundEffect[4], 0 );
    int flag_e = 0,flag_b=0, time[8] , ang[8] ;

    for (int i=0; i<8; ++i)
    {
        time[i] = 0;
        ang[i] = -60;
    }

    while (flag_b < 8)
    {
        if (time[flag_e] == 16 && flag_e <7) ++flag_e;

        if ((flag_b<4 && time[flag_b] == 21)||(flag_b>=4 && time[flag_b] == 31))
        {
            Square[flag_b%4][flag_b/4].PresentNumber = flag_b;
            ++flag_b;
        }

        for (int i=flag_b; i<=flag_e; ++i)
        {
            time[i]++;
            ang[i]+=2;
        }

        renderBackground();
        renderSquare();
        drawPowerBox(powerPoint);

        for (int i=flag_b; i<=flag_e; ++i)
        {
            //std::cout<<i<<'\n';
            gLetter[i].render(190+110*(i%4),time[i]*10,NULL,ang[i]);
        }
        SDL_RenderPresent(gRenderer);
        SDL_Delay(10);
    }

}

void fillBlank(int pos)
{
    Mix_PlayChannel( -1, GPSoundEffect[5], 0 );
    SDL_Event e;
    bool quit = false;
    SDL_Color TextColor = {0,0,0};
    string text = "";
    int letter ;

    bool del = false, add = false;

    CongratBoard.render(150,150);
    rankStar[pos].render(700,230);
    SDL_RenderPresent(gRenderer);
    while (!quit)
    {
        if (SDL_PollEvent( &e ) != 0)
        {
            if (e.type == SDL_QUIT) {quit = true; tryAgain=false;exitGame = true;}

            const Uint8* currentKeyStates = SDL_GetKeyboardState( NULL );

            if (currentKeyStates[ SDL_SCANCODE_LSHIFT ])
            {
                if (e.type == SDL_KEYDOWN)
                {
                    letter = e.key.keysym.sym - 32;
                    if ((letter>=65&&letter<=90)) add = true;
                }

            }
                else
            {
                if (e.type == SDL_KEYDOWN)
                {
                    letter = e.key.keysym.sym;
                    //cout<<letter<<endl;
                    if (letter == SDLK_BACKSPACE) del = true;
                    if (letter == SDLK_RETURN && text.length() > 0) {quit = true; gRank[pos].Name = text;}
                    if ((letter >= 97 && letter <= 122)||(letter>=48&&letter<=57)||letter==SDLK_SPACE) add = true;
                }

            }
        }

        if (add)
        {
            if (text.length() <18 ) text = text + (char)letter;
            add=false;
        }

        if (del)
        {
            if (text.length() > 0)text.erase(text.length()-1,1);
            del = false;
        }

        renderBackground();
        renderSquare();
        drawPowerBox(powerPoint);
        CongratBoard.render(150,150);
        rankStar[pos].render(700,230);

        if (text.length() > 0)
        {
            TextScore.loadFromRenderedText(text,TextColor);
            TextScore.render(300,450);
        }
        SDL_RenderPresent(gRenderer);
    }
}

void RenewScoreBoard()
{
    ofstream file_Name ("Text/Name.txt");
    ofstream file_Score ("Text/Score.txt");
    for (int i=0; i<5; ++i)
    {
        file_Name<<gRank[i].Name<<endl;
        file_Score<<gRank[i].Score<<endl;
    }

}

void finishGame()
{
    Mix_HaltMusic();
    endgame = true;

    performAnimation_FadedScreen();
    for (int i=0; i<4; ++i)
        for (int j=0; j<4; ++j)
            Square[i][j].PresentNumber = -1;

    performAnimation_dropGAMEOVER();

    Mix_VolumeMusic(100);
    Mix_PlayMusic(GPMusic[3],-1);

    bool fillName = false;
    int newRank;
    for (int i=0; i<sum_rank; ++i)
        if (score > gRank[i].Score)
        {
            fillName = true;
            for (int j=sum_rank-2; j>=i+1; --j)
                gRank[j] = gRank[j-1];
            gRank[i].Score = score;
            newRank = i;
            break;
        }

    SDL_Event e;
    bool quit = false;
    int flag = 0;
    while (!quit)
    {
        if (SDL_PollEvent( &e ) != 0)
        {
            if (e.type == SDL_QUIT) {quit = true;tryAgain=false;exitGame = true;}
            if (e.type == SDL_KEYDOWN)
            {
                if (e.key.keysym.sym == SDLK_RIGHT || e.key.keysym.sym == SDLK_LEFT) flag = 1-flag;
                if (e.key.keysym.sym == SDLK_RETURN)
                {
                    if (flag == 1) tryAgain = false; else ++tryAgain;
                    return ;
                    //return quit;
                }
            }
        }
        renderBackground();
        renderSquare();
        drawPowerBox(powerPoint);

        if (fillName)
        {
            fillBlank(newRank);
            if (!tryAgain) quit = true; else RenewScoreBoard();
            fillName = false;
            //cout<<quit;
        }

        BacktoMenu[flag].render(5,528);
        PlayAgain[1-flag].render(385,528);
        SDL_RenderPresent(gRenderer);
    }
    //cout<<quit;
}

bool checktoQuit()
{
    for (int i=0; i<4; ++i)
        for (int j=0; j<4; ++j)
            if (Square[i][j].PresentNumber == -1) return false;

    for (int i=0; i<4; ++i)
        for (int j=0; j<4; ++j)
        {
            if (iceNum[i][j]) continue;
            bool success = false;
            for (int k=0; k<4; ++k)
            {
                int inew,jnew;
                inew = i+ row[k];
                jnew = j+ column[k];
                if (inew<0||inew>3||jnew<0||jnew>3||iceNum[inew][jnew]||Square[inew][jnew].PresentNumber != Square[i][j].PresentNumber) continue;
                success = true;
                break;
            }
            if (success) return false;
        }
    return true;
}

void handleBonus()
{
    if (!merged) combo = 0;
    int oldPP = powerPoint;

    int maxx = -1;

    for (int i = 0; i < 4; ++i)
        for (int j=0; j < 4; ++j)
            if (Square[i][j].PresentNumber > maxx)
                maxx = Square[i][j].PresentNumber;

    switch (combo)
    {
        case 0 :
            powerPoint -= decPoint[maxx];
            break;
        case 1 :
            powerPoint += incPoint[maxx];
            break;
        case 2 :
            powerPoint += incPoint[maxx]*2;
            break;
        case 3 :
            powerPoint += incPoint[maxx]*3;
            break;
        case 4 :
            powerPoint += incPoint[maxx]*4;
            break;
        default : powerPoint += incPoint[maxx]*5;
    }
    powerPoint = std::max(0,powerPoint);
    powerPoint = std::min(100,powerPoint);

    performAnimation_ChangePowerBox(oldPP,powerPoint);

    if (powerPoint == 0)
    {
        performAnimation_FreezingSquare();
        performAnimation_ChangePowerBox(powerPoint,50);
        powerPoint = 50;
    }
    if (powerPoint == 100)
    {
        score = score + transferScore(maxx+1) * transferCombo(combo);
        performAnimation_x2Square();
        performAnimation_ChangePowerBox(powerPoint,50);
        powerPoint = 50;
    }
}

void writeNumber()
{
    for (int j=0;j<4;++j)
    {
        for (int i=0;i<4;++i)
            std::cout<<Square[i][j].PresentNumber<<' ';
        std::cout<<std::endl;
    }
     std::cout<<std::endl;
}

bool Play()
{
    //cout<<tryAgain;
    //if (tryAgain==0) return;
    tryAgain = 1;exitGame = false;volumn = 100;
    if( !init() )
    {
        printf( "Failed to initialize!\n" );
    }
    else
    {
        if (!loadMedia()) return true;
        while (tryAgain)
        {
            powerPoint = 50;combo = 0;score = 0;
            Mix_HaltMusic();
            createSquare();
            createIceNum();
            //iceNum[2][2] = true;iceNum[1][1] = true;iceNum[3][3] = true;
            createRandNum();
            createRandomNumber();
            createRandomNumber();
            creategImageBlendingMode();

            writeNumber();
            bool quit = false;
            endgame = false;
            appear = false;  merged = false;
            SDL_Event e;

            while( !quit )
            {
                if (checktoQuit()) {finishGame();break ;}
                //kiem tra xem con choi duoc nua hay ko

                performAnimation_QuickMove();
                //SDL_Delay(300);
                // chay hoat hinh khi di chuyen cac o

                staySquare();
                //Co dinh cac o
                if (appear) Mix_PlayChannel( -1, GPSoundEffect[0], 0 );

                if (appear) createRandomNumber();

                if (appear) performAnimation_AppearRandomSquare();
                //hieu ung xuat hien o moi

                if (appear) handleBonus();
                // xu li bonus

                renderBackground();
                renderSquare();
                drawPowerBox(powerPoint);

                createRandNum();

                SDL_RenderPresent( gRenderer );

                appear = false; merged = false;

                // khoi tao lai mang Ani
                //break;
                if ( SDL_PollEvent( &e ) != 0 )
                {

                    if( e.type == SDL_QUIT )
                    {
                        quit = true;tryAgain = false;exitGame = true;
                    }

                    if (e.type == SDL_KEYDOWN)
                        switch (e.key.keysym.sym)
                        {
                            case (SDLK_UP) :
                            {
                                for (int i = 0; i < 4; ++i)
                                {
                                    int pos = 0, preNum = -1;
                                    for (int j = 0; j < 4; ++j)
                                    {
                                        int Num = Square[i][j].PresentNumber;
                                        if (iceNum[i][j])
                                        {
                                            pos = j+1; preNum = -1;
                                            continue;
                                        }
                                        if (Num == -1) continue;
                                        if (preNum == -1 || preNum != Num)
                                        {
                                            if (j != pos) appear = true;

                                            Square[i][j] = ReFormatSquare(i, j, 0, -1, (j-pos)*22, Num, -1);
                                            preNum = Num;
                                            Square[i][pos].PresentNumber = Num;


                                            ++pos;
                                            continue;
                                        }
                                        appear = true;
                                        merged = true;

                                        Square[i][j] = ReFormatSquare(i, j, 0, -1, (j-pos+1)*22, Num, -1);
                                        Square[i][pos-1].PresentNumber +=1;
                                        score = score + transferScore(Square[i][pos-1].PresentNumber) *transferCombo(combo);
                                        combo = combo + 1 ;
                                        combo = std::min(combo,5);
                                        preNum = -1;

                                    }
                                }

                                writeNumber();
                                break;
                            }

                            case (SDLK_DOWN) :
                            {

                                for (int i = 0; i < 4; ++i)
                                {
                                    int pos = 3, preNum = -1 ;

                                    for (int j = 3; j >= 0; --j)
                                    {
                                        int Num = Square[i][j].PresentNumber;

                                        if (iceNum[i][j])
                                        {
                                            pos = j-1; preNum = -1;
                                            continue;
                                        }

                                        if (Num == -1) continue;

                                        if (preNum == -1 || preNum != Num)
                                        {
                                            if (j != pos) appear = true;

                                            Square[i][j] = ReFormatSquare(i, j, 0, 1, (pos-j)*22, Num, -1);
                                            preNum = Num;
                                            Square[i][pos].PresentNumber = Num;

                                            --pos;
                                            continue;
                                        }
                                        appear = true;
                                        merged = true;

                                        Square[i][j] = ReFormatSquare(i, j, 0, 1, (pos+1-j)*22, Num, -1);
                                        Square[i][pos+1].PresentNumber +=1;
                                        score = score + transferScore(Square[i][pos+1].PresentNumber) *transferCombo(combo);
                                        combo = combo + 1 ;
                                        combo = std::min(combo,5);
                                        preNum = -1;
                                    }
                                }
                                writeNumber();
                                break;
                            }

                            case (SDLK_LEFT) :
                            {

                                for (int j = 0; j < 4; ++j)
                                {
                                    int pos = 0, preNum = -1 ;

                                    for (int i = 0; i < 4; ++i)
                                    {
                                        int Num = Square[i][j].PresentNumber;

                                        if (iceNum[i][j])
                                        {
                                            pos = i+1; preNum = -1;
                                            continue;
                                        }

                                        if (Num == -1) continue;

                                        if (preNum == -1 || preNum != Num)
                                        {
                                            if (i != pos ) appear = true;

                                            Square[i][j] = ReFormatSquare(i, j, -1, 0, (i-pos)*22, Num, -1);
                                            preNum = Num;
                                            Square[pos][j].PresentNumber = Num;

                                            ++pos;
                                            continue;
                                        }
                                        appear = true;
                                        merged = true;

                                        Square[i][j] = ReFormatSquare(i, j, -1, 0, (i-pos+1)*22, Num, -1);
                                        Square[pos-1][j].PresentNumber +=1;
                                        score = score + transferScore(Square[pos-1][j].PresentNumber) *transferCombo(combo);
                                        combo = combo + 1 ;
                                        combo = std::min(combo,5);
                                        preNum = -1;
                                    }
                                }
                                writeNumber();
                                break;
                            }

                            case (SDLK_RIGHT) :
                            {

                                for (int j = 0; j < 4; ++j)
                                {
                                    int pos = 3, preNum = -1 ;

                                    for (int i = 3; i >= 0; --i)
                                    {
                                        int Num = Square[i][j].PresentNumber;

                                        if (iceNum[i][j])
                                        {
                                            pos = i-1; preNum = -1;
                                            continue;
                                        }

                                        if (Num == -1) continue;

                                        if (preNum == -1 || preNum != Num)
                                        {
                                            if (i != pos) appear = true;

                                            Square[i][j] = ReFormatSquare(i, j, 1, 0, (pos-i)*22, Num, -1);
                                            preNum = Num;
                                            Square[pos][j].PresentNumber = Num;

                                            --pos;
                                            continue;
                                        }
                                        appear = true;
                                        merged = true;

                                        Square[i][j] = ReFormatSquare(i, j, 1, 0, (pos+1-i)*22, Num, -1);
                                        Square[pos+1][j].PresentNumber +=1;
                                        score = score + transferScore(Square[pos+1][j].PresentNumber) *transferCombo(combo);
                                        combo = combo + 1 ;
                                        combo = std::min(combo,5);
                                        preNum = -1;
                                    }
                                }
                                writeNumber();
                                break;
                            }
                        }
                    }

            }
        }
        close();
    }
    return exitGame;
}


