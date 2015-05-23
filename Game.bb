;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            3D арена-шутер от первого лица 
;==================================================================

;Подключение всех модулей
Include "code\Engine.bb"
Include "code\tools.bb"
Include "code\Enemy.bb"
Include "code\Mouse.bb"
Include "code\Window.bb"
Include "code\Player.bb"
Include "code\Level.bb"
Include "code\Spawner.bb"
Include "code\Blood.bb"
Include "code\Guns.bb"
Include "code\Flash.bb"
Include "code\Rocket.bb"
Include "code\Explosion.bb"
Include "code\Display.bb"
Include "code\Items.bb"
Include "code\Soot.bb"
Include "code\Doors.bb"
Include "code\Torch.bb"
Include "code\Music.bb"

;Режимы игры
Const GameStatusMainMenu  = 1
Const GameStatusAboutMenu = 2
Const GameStatusPauseMenu = 3	
Const GameStatusDiedMenu  = 4 
Const GameStatusBreak     = 5
Const GameStatusBattle    = 6
Const GameStatusGameOver  = 8

;Основные глобальные переменные
Global GameStatus = GameStatusMainMenu
Global GameVawe   = 1
Global GameMillis 
Global GameSpawnTime
Global GameVaweSplash
Global GameBreakTimer = 0
Global GameBreakTime  = 1000 * 15
Global GameVaweIndex  = 1
Global GameVaweStep   = 1
Global GameVaweEnemy  = 0
Global GameVaweTotal  = 0
Global GameVaweDied   = 0
Global GameVaweAtTim  = 0
Global GamePaused     = 0

;Инициализация всех модулей
InitGame()
InitWindow()
InitMusic()
InitDisplay()
InitBlood()
InitTorch()
InitPlayer()
InitLevel()
InitFlashes()
InitGuns()
InitRocket()
InitEnemy()
InitExplosin()
InitItems()
InitSoots()
InitDoors()

;Основной цикл
While Not xWinMessage("WM_CLOSE")
	;Очищение и обновление 
	xCls
	xUpdateWorld

	;Переменная, т.к. xMillisecs используется многократно за цикл
	GameMillis = xMillisecs()

	;Включение и выключение меню паузы
	If xKeyHit(KEY_ESCAPE)
		GamePaused = Not GamePaused
		If GamePaused And (GameStatus = GameStatusBreak Or GameStatus = GameStatusBattle) GameStatus = GameStatusPauseMenu
		If Not GamePaused
			If GameVaweAtTim > 0 Gamestatus = GameStatusBattle Else Gamestatus = GameStatusBreak
		End If
	End If

	;Выборка режима игры
	Select GameStatus
		Case GameStatusBreak
			UpdateGame()

			;При обнулении счетчика начать волну
			If GameBreakTimer < GameMillis
				GameStatus = GameStatusBattle
				OpenDoors()
				
				xPlaySound PlayerSoundTaunt(Rand(0, PlayerTauntsCount))
				MusicStatus = MusicStatusBattle
				MusicTaunt()
			End If
		Case GameStatusBattle
			UpdateGame()

			;Если врагов меньше необходимого, активировать новых
			If GameVaweEnemy < GameVaweIndex * GameVaweStep * GameFactorEnemy
				SpawnEnemys((GameVaweIndex * GameVaweStep * GameFactorEnemy) - GameVaweEnemy)
			End If

			;Генерация экстренных аптечек и патронов
			If ExtrimalAmmoSpawned   = False And GunShotGunAmmo <=10 CreateItem(ItemAmmoShotGun, True)
			If ExtrimalHealthSpawned = False And PlayerHealth <= 100 CreateItem(ItemHealthSmall, True)

		Case GameStatusGameOver
			UpdateGameover()
	End Select

	;Отрисовка
	xRenderWorld
	
	;Обновление музыки и интерфейса
	UpdateMusic()
	UpdateDisplay()
	
	xFlip
Wend

End

;Проверка на завершения этапа или всей волны
Function CheckForNewStep()
	If GameVaweDied = GameVaweIndex * GameVaweStep * GameFactorEnemy
		;Завершение волны
		If GameVaweStep = 3
			GameStatus = GameStatusBreak
			GameVaweEnemy  = 0
			GameVaweDied   = 0
			GameVaweStep   = 1
			GameVaweIndex  = GameVaweIndex + 1
			GameBreakTimer = GameMillis + GameBreakTime
			ExtrimalItemSpawned   = False
			ExtrimalHealthSpawned = False
			CloseDoors()

			MusicStatus = MusicStatusBreak
			SpawnItemsVawe()
		;Завершение этапа
		Else
			GameVaweStep  = GameVaweStep + 1
			GameVaweEnemy = 0
			GameVaweDied  = 0
			ExtrimalItemSpawned   = False
			ExtrimalHealthSpawned = False
	
			SpawnItemsBreak()
			MusicTaunt()
			xPlaySound PlayerSoundTaunt(Rand(0, PlayerTauntsCount))
		End If
	End If
End Function

;Инициализация игры
Function InitGame()
	;Типы столкновений
	xCollisions CollType_Player, CollType_Level, SPHERETOTRIMESH, RESPONSE_SLIDING
	xCollisions CollType_Enemy,  CollType_Level, SPHERETOTRIMESH, RESPONSE_SLIDING
	xCollisions CollType_Rocket, CollType_Level, SPHERETOTRIMESH, RESPONSE_SLIDING

	; loading options:
	ConfigFile$ = ReadFile("options.ini")

	;Чтение файла настроек
	If ConfigFile
		While Not(Eof(ConfigFile))
			ConfigLine$ = ReadLine(ConfigFile)
			Parametr$   = StrCut(ConfigLine, 1, "=")
			ValueInt% = Int(StrCut(ConfigLine, 2, "="))
			ValueFlt# = Float(StrCut(ConfigLine, 2, "="))

			Select Lower(Parametr)
				Case "windowmode"    WindowMode   = ValueInt
				Case "windowwidth"   WindowWidth  = ValueInt
				Case "windowheight"  WindowHeight = ValueInt
				Case "windowdepth"   WindowDepth  = ValueInt
				Case "windowvsync"   WindowVsync  = ValueInt
				Case "windowantial"  WindowAntial = ValueInt
				Case "mousesens"     MouseSens    = ValueFlt
				Case "emenyperframe" GameMaxEnemy = ValueInt
				Case "enemyspeed"    EnemySpeed   = ValueFlt
				Case "enemyforce"    EnemyForce   = ValueInt
				Case "debugfps"      DisplayDrawFps    = ValueInt
				Case "debugmode"     DisplayDebugMode  = ValueInt
				Case "musicvolume"	 MusicMasterVolume = ValueFlt
			End Select
		Wend
		CloseFile(ConfigFile)
		If DisplayDebugMode xCreateLog(1, LOG_INFO)
	End If
End Function

;Обновление игры
Function UpdateGame()
	If Not GamePaused
		UpdateMouse()
		UpdateGuns()
		UpdateEnemys() 
		UpdateBlood()
		UpdateFlashes()
		UpdateRockets()
		UpdatePlayer()
		UpdateLevel()
		UpdateExplosions()
		UpdateItems()
		UpdateSoots()
		UpdateDoors()
		UpdateTorches()		
	End If
End Function

;Обновление при гейм-овере
Function UpdateGameOver()
	UpdateTorches()
	UpdateDoors()
	UpdateItems()
	UpdateExplosions()
	UpdateLevel()
	UpdateBlood()
	UpdateFlashes()
	UpdateRockets()
End Function 

;Сброс всего
Function ResetGame()
	GameStatus     = GameStatusMainMenu
	GameBreakTimer = GameMillis + GameBreakTime
	GameSpawnTime  = 0
	GameVaweSplash = 0
	GameVaweIndex  = 1
	GameVaweStep   = 1
	GameVaweEnemy  = 0
	GameVaweTotal  = 0
	GameVaweDied   = 0
	GameVaweAtTim  = 0
	GamePaused     = 0
	
	ResetPlayer()
	ResetEmenys()
	ResetItems()
	ResetGuns()
	ResetBlood()
	ResetSoot()
	ResetRockets()
	ResetDoors()
	ResetLevel()

	MusicStatus = MusicStatusBreak
End Function

;Запуск игры
Function StartGame()
	ResetGame()
	CreateGun(GunNameShotGun)
	GameStatus     = GameStatusBreak
	GameBreakTimer = GameMillis + GameBreakTime
End Function
