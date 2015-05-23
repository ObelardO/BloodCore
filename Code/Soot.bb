;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Мдуль игрока
;==================================================================

;Тип коллизии игрока
Const CollType_Player = 2

;Кол-во фраз игрока
Const PlayerTauntsCount = 13

;Высота игрока
Const PlayerHeight# = 2.0

;Все параметры и объекты игрока
Global PlayerCollider
Global PlayerSpeed# = 0.8
Global PlayerCamera
Global PlayerLight
Global PlayerGunPoint
Global PlayerCrossPoint
Global PlayerX#
Global PlayerZ#
Global PlayerPitch#
Global PlayerYaw#
Global PlayerMoving
Global PlayerIconsTexture
Global PlayerScore
Global PlayerBestScore
Global PlayerCombo 
Global PlayerShotDistance
Global PlayerCrossTexture
Global PlayerHealth = 1000
Global PlayerArmo   = 0

;Параметры иконки брони игрока
Global PlayerArmoImageID 
Global PlayerArmoImageX 
Global PlayerArmoImageY 
Global PlayerArmoTextX 
Global PlayerArmoTextY 

;Параметры иконки здоровья игрока
Global PlayerHealthImageID 
Global PlayerHealttImageX 
Global PlayerHealthImageY 
Global PlayerHealthTextX
Global PlayerHealthTextY 

;Параметры изображения прицела
Global PlayerCrossImageX 
Global PlayerCrossImageY 

;Параметры эффектов игрока
Global PlayerEffectItem#
Global PlayerEffectBlood#

;Звуки игрока
Dim PlayerSoundTaunt(PlayerTauntsCount)
Global PlayerSoundDeath

;Инициализация игрока
Function InitPlayer()
	PlayerIconsTexture = xLoadAnimImage("base\Images\Icons.png",IconsImageFrameSize,IconsImageFrameSize, 0, 8)
	PlayerCrossTexture = xLoadImage("base\images\Cross.png")
	PlayerSoundDeath   = xLoadSound("base\sounds\player\Death.wav")

	PlayerCollider = xCreateSphere(8)
	xPositionEntity PlayerCollider, 0.0, 1.0, 0.0
	xEntityType     PlayerCollider, CollType_Player
	xEntityAlpha    PlayerCollider, 0.0

	PlayerCamera = xCreateCamera(PlayerCollider)
	xPositionEntity PlayerCamera, 0.0, PlayerHeight, 0.0
	xRotateEntity   PlayerCamera, 0.0, 0.0, 0.0
	xCameraRange    PlayerCamera, 0.01, 400
	xCreateListener PlayerCamera

	PlayerCrossPoint = xCreatePivot(PlayerCollider)
	xPositionEntity PlayerCrossPoint, 0.0, 0.0, 0.0
	xScaleEntity    PlayerCrossPoint, 2.0, 2.0, 2.0
	xMoveEntity     PlayerCrossPoint, 0.0, 0.0, 100

	PlayerLight = xCreateLight(LIGHT_POINT);, PlayerCollider)
	xEntityParent PlayerLight, PlayerCollider
	xLightColor   PlayerLight, 255, 220, 132
	xLightRange   PlayerLight, 15
	xHideEntity   PlayerLight
	xMoveEntity   PlayerLight, 0, PlayerHeight, 2

	For i = 0 To PlayerTauntsCount
		PlayerSoundTaunt(i) = xLoadSound("base\sounds\player\taunt" +(i + 1)+ ".mp3")
	Next

	PlayerArmoImageID = 1
	PlayerArmoImageX  = 50
	PlayerArmoImageY  = WindowHeight - IconsImageFrameSize - 90
	PlayerArmoTextX   = PlayerArmoImageX + IconsImageFrameSize + 8
	PlayerArmoTextY   = PlayerArmoImageY + 1

	PlayerHealthImageID = 0
	PlayerHealttImageX  = 50
	PlayerHealthImageY  = WindowHeight - IconsImageFrameSize - 50
	PlayerHealthTextX   = PlayerHealttImageX + IconsImageFrameSize + 8
	PlayerHealthTextY   = PlayerHealthImageY + 1

	PlayerCrossImageX = (WindowWidth  - 64) * 0.5
	PlayerCrossImageY = (WindowHeight - 64) * 0.5
End Function

;Обновление игрока
Function UpdatePlayer()
	;mouselook
	xTurnEntity PlayerCollider, 0,-MouseSpeedX * MouseSens, 0
	PlayerYaw  = xEntityYaw(PlayerCollider)

	PlayerPitch = PlayerPitch + MouseSpeedY * MouseSens
	
	;angle 0-90
	If PlayerPitch >  90 PlayerPitch = 90
	If PlayerPitch < -89 PlayerPitch =-89

	xRotateEntity PlayerCamera, PlayerPitch, 0, 0

	;movement
	PlayerMoving = False
	If xKeyDown(KEY_W) Or xKeyDown(KEY_UP)    xMoveEntity PlayerCollider, 0, 0, 0.5 * PlayerSpeed PlayerMoving = True
	If xKeyDown(KEY_S) Or xKeyDown(KEY_DOWN)  xMoveEntity PlayerCollider, 0, 0, 0.5 *-PlayerSpeed PlayerMoving = True
	If xKeyDown(KEY_A) Or xKeyDown(KEY_LEFT)  xMoveEntity PlayerCollider, 0.5 *-PlayerSpeed, 0, 0 PlayerMoving = True
	If xKeyDown(KEY_D) Or xKeyDown(KEY_RIGHT) xMoveEntity PlayerCollider, 0.5 * PlayerSpeed, 0, 0 PlayerMoving = True

	PlayerX# = xEntityX(PlayerCollider)
	PlayerZ# = xEntityZ(PlayerCollider)

	xPositionEntity PlayerCollider, PlayerX, 1.0, PlayerZ

	If PlayerEffectItem  > 0.0 PlayerEffectItem  = PlayerEffectItem - 0.05
	If PlayerEffectBlood > 0.0 PlayerEffectBlood = PlayerEffectBlood - 0.01

	If (GunFireLeft Or GunFireRight) xShowEntity PlayerLight Else xHideEntity PlayerLight

	If PlayerHealth <= 0
		PlayerHealth = 0
		GameStatus = GameStatusGameOver
		xPositionEntity PlayerCamera, 0.0, 0.10, 0.0
		xRotateEntity   PlayerCamera, 0.0, 0.0, 30.0
		ResetGuns()
		xPlaySound PlayerSoundDeath
	End If
End Function

;Сброс игрока
Function ResetPlayer()
	If PlayerBestScore < PlayerScore
		PlayerBestScore = PlayerScore
		SaveBestScore()
	End If
	PlayerScore  = 0
	PlayerCombo  = 0
	PlayerSpeed  = 1.0
	PlayerHealth = 1000
	PlayerArmo   = 0
	PlayerEffectItem  = 0
	PlayerEffectBlood = 0
	PlayerX = 0
	PlayerZ = 0
	PlayerYaw   = 0
	PlayerPitch = 0
	xPositionEntity PlayerCollider, 0, 1.0, 0
	xRotateEntity   PlayerCollider, 0, 0, 0
	xPositionEntity PlayerCamera, 0.0, PlayerHeight, 0.0
	xRotateEntity   PlayerCamera, 0.0, 0.0, 0.0

	LoadBestScore()
End Function

;Загрузка лучшего счета
Function LoadBestScore()
	ScoreFile$ = ReadFile("base\score.txt")
	If ScoreFile PlayerBestScore = Int(ReadLine(ScoreFile)) CloseFile(ScoreFile)
End Function

;Сохранение лучшего счета
Function SaveBestScore()
	ScoreFile$ = OpenFile("base\score.txt")
	WriteLine(ScoreFile, Str(PlayerBestScore))
	CloseFile(ScoreFile)
End Function
