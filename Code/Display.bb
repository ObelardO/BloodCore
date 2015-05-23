;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль игрового интерфейса
;==================================================================

;Режим отображения текста
Const TextAlignLeft  = 0
Const TextAlignRight = 1
Const TextAlignCenter= 2

;Размер иконок для отображения
Const IconsImageFrameSize = 32

;Переменные для шрифтов
Global DisplayFont30
Global DisplayFont20
Global DisplayFont9

;Режимы работы интерфейса для дебага
Global DisplayDebugMode
Global DisplayDrawFps

;Оверлейные текстуры
Global DisplayImageOverlay
Global DisplayImageBlood

;Текстура загрузочного экрана
Global DisplayImageLoading

;Параметры для отображения меню
Global DisplayImageLogo
Global DisplayMenuWidth 
Global DisplayMenuX 
Global DisplayMenuButtonWidth 
Global DisplayMenuButtonHeight 
Global DisplayMenuMouseX
Global DisplayMenuMouseY

;Инициализация модуля интерфейса
Function InitDisplay()
	DisplayFont30 = xLoadFont("Arial", 30, True)
	DisplayFont20 = xLoadFont("Arial", 20, True)
	DisplayFont9  = xLoadFont("Arial", 9)

	DisplayImageOverlay = xLoadImage("base\Images\overlay.png")
	xResizeImage DisplayImageOverlay, WindowWidth, WindowHeight

	DisplayImageBlood = xLoadImage("base\Images\blood.png")
	xResizeImage DisplayImageBlood, WindowWidth, WindowHeight

	DisplayImageLoading = xLoadImage("base\Images\loading.jpg")
	xResizeImage DisplayImageLoading, WindowWidth, WindowHeight

	DisplayImageLogo = xLoadImage("base\Images\logo.png")

	DisplayMenuWidth = 500
	DisplayMenuX = (WindowWidth - DisplayMenuWidth) * 0.5
	DisplayMenuButtonWidth = 500
	DisplayMenuButtonHeight = 70

	DrawLoadingScreen()
End Function

;Отрисовка игрового лого
Function DisplayDrawLogo()
	xColor 0, 0, 0, 96
	xRect 0, 0, WindowWidth, WindowHeight, True
	xColor 127, 0, 0, 255
	xRect DisplayMenuX, 500, DisplayMenuWidth, WindowHeight, True
	xDrawImage DisplayImageLogo, DisplayMenuX, 0
End Function

;Отрисовка кнопки
Function DisplayDrawButton(ButtonX, ButtonY, ButtonCaption$, ButtonFont)
	If DisplayMouseInRect(ButtonX, ButtonY, DisplayMenuButtonWidth, DisplayMenuButtonHeight)
		xColor 255, 255, 255, 255
		xRect  ButtonX, ButtonY, DisplayMenuButtonWidth, DisplayMenuButtonHeight, True
		DisplayText(ButtonX + DisplayMenuButtonWidth * 0.5, ButtonY + 15, ButtonCaption, ButtonFont, TextAlignCenter, 1.0, 127, 0, 0)
		If xMouseHit(MOUSE_LEFT)
			xFlushMouse()
			Return True
		End If
	Else
	 	DisplayText(ButtonX + DisplayMenuButtonWidth * 0.5, ButtonY + 15, ButtonCaption, ButtonFont, TextAlignCenter, 1.0)
	End If
End Function

;Проверка на наличие курсора в области
Function DisplayMouseInRect(RectX, RectY, RectW, RectH)
	If DisplayMenuMouseX > RectX And DisplayMenuMouseX < RectX + RectW
	If DisplayMenuMouseY > RectY And DisplayMenuMouseY < RectY + RectH
		Return True
	End If
	End If
	
	Return False
End Function

;Обновление интерфейса
Function UpdateDisplay()
	;Фиксирование координат курсора
	DisplayMenuMouseX = xMouseX()
	DisplayMenuMouseY = xMouseY()

	;Выборка игрового режима
	Select GameStatus
		;Отрисовка гавного меню
		Case GameStatusMainMenu
			DisplayDrawLogo()
			
			If DisplayDrawButton(DisplayMenuX, WindowHeight * 0.5 + DisplayMenuButtonHeight * 1, "FIGHT", DisplayFont30)
				StartGame()
			End If
			If DisplayDrawButton(DisplayMenuX, WindowHeight * 0.5 + DisplayMenuButtonHeight * 2, "ABOUT", DisplayFont30)
				GameStatus = GameStatusAboutMenu
			End If
			If DisplayDrawButton(DisplayMenuX, WindowHeight * 0.5 + DisplayMenuButtonHeight * 3, "EXIT", DisplayFont30)
				End 
			End If
			
			xDrawImage DisplayImageOverlay, 0, 0
			xDrawImage PlayerCrossTexture, DisplayMenuMouseX - 32, DisplayMenuMouseY - 32
			xFlushMouse()

		;Отрисовка меню "об игре"
		Case GameStatusAboutMenu
			DisplayDrawLogo()
			
			DisplayText(DisplayMenuX + DisplayMenuWidth * 0.5, WindowHeight * 0.5 + DisplayMenuButtonHeight * 1, "2015 (c) OBELARDO", DisplayFont20, TextAlignCenter, 1.0)
			DisplayText(DisplayMenuX + DisplayMenuWidth * 0.5, WindowHeight * 0.5 + DisplayMenuButtonHeight * 1 + 50, "obelardos@gmail.com", DisplayFont20, TextAlignCenter, 1.0)
			DisplayText(DisplayMenuX + DisplayMenuWidth * 0.5, WindowHeight * 0.5 + DisplayMenuButtonHeight * 1 + 100, "for igdc.ru #123", DisplayFont9, TextAlignCenter, 1.0)

			If DisplayDrawButton(DisplayMenuX, WindowHeight * 0.5 + DisplayMenuButtonHeight * 3, "BACK", DisplayFont30)
				GameStatus = GameStatusMainMenu 
			End If
			
			xDrawImage DisplayImageOverlay, 0, 0
			xDrawImage PlayerCrossTexture, DisplayMenuMouseX - 32, DisplayMenuMouseY - 32
			xFlushMouse()

		;Отрисовка меню паузы
		Case GameStatusPauseMenu
			DisplayDrawLogo()
			
			If DisplayDrawButton(DisplayMenuX, WindowHeight * 0.5 + DisplayMenuButtonHeight * 1, "FIGHT", DisplayFont30)
				If GameVaweAtTim > 0 Gamestatus = GameStatusBattle Else Gamestatus = GameStatusBreak
				GamePaused = False
			End If
			If DisplayDrawButton(DisplayMenuX, WindowHeight * 0.5 + DisplayMenuButtonHeight * 2, "EXIT", DisplayFont30)
				ResetGame()
			End If
			
			xDrawImage DisplayImageOverlay, 0, 0
			xDrawImage PlayerCrossTexture, DisplayMenuMouseX - 32, DisplayMenuMouseY - 32
			xFlushMouse()

		;Отрисовка геймовера
		Case GameStatusGameOver
			xColor 128,0,0, 128
			xRect 0, 0, WindowWidth, WindowHeight, True
			xImageAlpha DisplayImageBlood, 1.0
			xDrawImage  DisplayImageBlood, 0, 0
		
			DisplayText(DisplayMenuX + DisplayMenuWidth * 0.5, WindowHeight * 0.4, "YOU DIED", DisplayFont30, TextAlignCenter, 1.0)
			DisplayText(DisplayMenuX + DisplayMenuWidth * 0.5, WindowHeight * 0.4 + 50, "score: " + PlayerScore, DisplayFont20, TextAlignCenter, 1.0)

			If PlayerScore < PlayerBestScore
				DisplayText(DisplayMenuX + DisplayMenuWidth * 0.5, WindowHeight * 0.4 + 85, "best score: " + PlayerBestScore, DisplayFont9, TextAlignCenter, 1.0)
			Else	
				DisplayText(DisplayMenuX + DisplayMenuWidth * 0.5, WindowHeight * 0.4 + 85, "previous score: " + PlayerBestScore, DisplayFont9, TextAlignCenter, 1.0)
			End If
			If DisplayDrawButton(DisplayMenuX, WindowHeight * 0.5 + DisplayMenuButtonHeight * 2, "FIGHT AGAIN", DisplayFont30)
				StartGame()
			End If
			If DisplayDrawButton(DisplayMenuX, WindowHeight * 0.5 + DisplayMenuButtonHeight * 3, "GET OUT", DisplayFont30)
				ResetGame() 
			End If
			
			xDrawImage DisplayImageOverlay, 0, 0
			xDrawImage PlayerCrossTexture, DisplayMenuMouseX - 32, DisplayMenuMouseY - 32
			xFlushMouse()

		;Отрисовка в режиме паузы между волнами или во время волны
		Default
			;Отрисовка эффекта крови
			If PlayerEffectBlood
				xImageAlpha DisplayImageBlood, PlayerEffectBlood
				xDrawImage  DisplayImageBlood, 0, 0
				If PlayerEffectBlood > 1.0
					xColor 128,0,0, 196 * (PlayerEffectBlood - 4.0)
					xRect 0, 0, WindowWidth, WindowHeight, True
				Else
					xColor 128,0,0, 96 * (PlayerEffectBlood)
					xRect 0, 0, WindowWidth, WindowHeight, True
				End If
			End If

			;Отрисовка эффекта при сборе прдметов 
			If PlayerEffectItem > 0.0
				xColor 2,253,165, 128 * PlayerEffectItem
				xRect 0, 0, WindowWidth, WindowHeight, True
			End If

			;Отрисовка прицела
			xDrawImage PlayerCrossTexture, PlayerCrossImageX, PlayerCrossImageY

			;Отрисовка игрового счета
			xColor 0, 0, 0, 128
			xRect WindowWidth * 0.5 - IconsImageFrameSize * 2, 50, IconsImageFrameSize * 4, IconsImageFrameSize, True
			DisplayText WindowWidth * 0.5, 51, Str(String("0", 6 - Len(PlayerScore)) + PlayerScore), DisplayFont20, TextAlignCenter

			;Отрисовка комбинация
			If PlayerCombo > 1
				Displace# = Float(EnemyLastDieTime - GameMillis) / EnemyForComboTime
				DisplayText WindowWidth * 0.5, 120 - 20 * (1 - Displace), Str("x" + PlayerCombo), DisplayFont30, TextAlignCenter, Displace
			End If

			;Отрисовка счетчика до начала волны
			If GameStatus = GameStatusBreak
				DisplayText WindowWidth * 0.5, 100, "battle will start in:", DisplayFont9, TextAlignCenter, 1.0
				DisplayText WindowWidth * 0.5, 120, (GameBreakTimer - GameMillis) / 1000, DisplayFont30, TextAlignCenter, 1.0
			End If

			;Отрисовка здоровья
			xColor 0, 0, 0, 128
			xImageAlpha PlayerIconsTexture, 1.0
			xRect       PlayerHealttImageX, PlayerHealthImageY, IconsImageFrameSize * 3, IconsImageFrameSize, True
			xDrawImage  PlayerIconsTexture, PlayerHealttImageX, PlayerHealthImageY, PlayerHealthImageID
			DisplayText PlayerHealthTextX,  PlayerHealthTextY,  Int(PlayerHealth * 0.1), DisplayFont20

			;Отрисовка защиты
			If PlayerArmo
				xColor 0, 0, 0, 128
				xImageAlpha PlayerIconsTexture, 1.0
				xRect       PlayerArmoImageX, PlayerArmoImageY, IconsImageFrameSize * 3, IconsImageFrameSize, True
				xDrawImage  PlayerIconsTexture, PlayerArmoImageX, PlayerArmoImageY, PlayerArmoImageID
				DisplayText PlayerArmoTextX,  PlayerArmoTextY,  Int(PlayerArmo * 0.1), DisplayFont20
			End If

			;Отрисовка патронов дрбовика
			If GunShotGunAdded
				If GunSelected = GunNameShotGun Alpha# = 1.0 Else Alpha# = 0.5
				xColor 0, 0, 0, 128 * Alpha
				xImageAlpha PlayerIconsTexture, Alpha
				xRect       GunShotGunImageX - GunShotGunImageSize * 2, GunShotGunImageY, GunShotGunImageSize * 3, GunShotGunImageSize, True
				xDrawImage  PlayerIconsTexture, GunShotGunImageX, GunShotGunImageY, GunShotGunImageID
				DisplayText GunShotGunImageX - 8,  GunShotGunImageY + 1,  GunShotGunAmmo, DisplayFont20, TextAlignRight, Alpha
			End If

			;Отрисовка патронов пулемета
			If GunMiniGunAdded
				If GunSelected = GunNameMiniGun Alpha# = 1.0 Else Alpha# = 0.5
				xColor 0, 0, 0, 128 * Alpha
				xImageAlpha PlayerIconsTexture, Alpha
				xRect       GunMiniGunImageX - GunMinigunImageSize * 2, GunMiniGunImageY, GunMinigunImageSize * 3, GunMinigunImageSize, True
				xDrawImage  PlayerIconsTexture, GunMiniGunImageX, GunMiniGunImageY, GunMiniGunImageID
				DisplayText GunMiniGunImageX - 8,  GunMiniGunImageY+ 1,  GunMiniGunAmmo, DisplayFont20, TextAlignRight, Alpha
			End If

			;Отрисовка ракет
			If GunRocketAdded
				If GunSelected = GunNameRocket Alpha# = 1.0 Else Alpha# = 0.5
				xColor 0, 0, 0, 128 * Alpha
				xImageAlpha PlayerIconsTexture, Alpha
				xRect       GunRocketImageX - GunRocketImageSize * 2, GunRocketImageY, GunRocketImageSize * 3, GunRocketImageSize, True
				xDrawImage  PlayerIconsTexture, GunRocketImageX, GunRocketImageY, GunRocketImageID
				DisplayText GunRocketImageX - 8,  GunRocketImageY+1,  GunRocketAmmo, DisplayFont20, TextAlignRight, Alpha
			End If

			;Отрисовка затемнений по углам
			xDrawImage DisplayImageOverlay, 0, 0
	End Select

	;Отрисовка различных параметров в режиме дебага
	If DisplayDebugMode
		xColor 255, 255, 255, 255
		xSetFont DisplayFont9
		xText 10, 10,  "FPS " + xGetFPS() + "  TRIS " + xTrisRendered() + "  DIP " + xDIPCounter()
		xText 10, 30,  "PLAYER XYZ" + xEntityX(PlayerCollider) + " " +  xEntityY(PlayerCollider) + " " +  xEntityZ(PlayerCollider) + "     YP " + PlayerYaw + " " + PlayerPitch
		xText 10, 50,  "VAWE " + GameVaweIndex
		xText 10, 70,  "STEP " + GameVaweStep
		xText 10, 90,  "ENEMY AT STEP " + GameVaweIndex * GameVaweStep * GameFactorEnemy
		xText 10, 110, "ENEMY AT SCREEN " + GameVaweEnemy
		xText 10, 130, "ANEMY DIED " + GameVaweDied
		xText 10, 150, "SCR "  +GameVaweAtTim
		xText 10, 170, "GUN " + GunSelected
		xText 10, 190, "FIRE " + GunFireLeft + " " + GunFireRight
		xText 10, 210, "GAME " + GameStatus
	ElseIf DisplayDrawFps
		xColor 255, 255, 255, 128
		xSetFont DisplayFont9
		xText 10, 10,  "FPS " + xGetFPS()
	End If
End Function

;Отрисовка текста
Function DisplayText(CaptionX, CaptionY, Caption$, CaptionFont, CaptionAlign = 0, CaptionAlpha# = 1.0, CaptionR = 255, CaptionG = 255, CaptionB = 255)
	xSetFont CaptionFont

	CaptionWidth = xStringWidth(Caption)

	Select CaptionAlign
		Case TextAlignLeft   :CaptionX = CaptionX
		Case TextAlignRight  :CaptionX = CaptionX - CaptionWidth
		Case TextAlignCenter :CaptionX = CaptionX - CaptionWidth * 0.5
	End Select

	xColor 0, 0, 0, 64 * CaptionAlpha
	xText CaptionX + 1, CaptionY + 1, Caption

	xColor CaptionR, CaptionG, CaptionB, 255 * CaptionAlpha
	xText CaptionX, CaptionY, Caption
End Function

;Отрисовка экрана загрузки
Function DrawLoadingScreen()
	xDrawImage DisplayImageLoading, 0, 0
	xFlip
End Function
