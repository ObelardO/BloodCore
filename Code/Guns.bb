;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль оружий
;==================================================================

;Констаты имен оружий
Const GunNameShotGun = 1
Const GunNameMiniGun = 2
Const GunNameRocket  = 3

;Константы скорострельности оружий
Const GunSpeedShotGun = 800
Const GunSpeedMiniGun = 200
Const GunSpeedRocket  = 1000

;Режимы оружий
Const GunStatusNone     = 0
Const GunStausStarting  = 1
Const GunStatusReady    = 2
Const GunStatusFireUp   = 3
Const GunStatusFireDown = 4
Const GunStatusFireing  = 5
Const GunStatusFinish   = 6

;Тип оружий, леворукое/праворукое
Const GunArmRight = 0
Const GunArmLeft  = 1

;Максимальное количество оружий
Const GunMaxCount = 3

;Максимальное количество патронов
Const GunShotGunMaxAmmo = 980
Const GunMiniGunMaxAmmo = 999
Const GunRocketMaxAmmo  = 980

;Модели оружий
Global GunModelShotGun
Global GunModelMiniGun
Global GunModelRocket

;Кол-во потронов в оружиях
Global GunShotGunAmmo
Global GunMiniGunAmmo
Global GunRocketAmmo

;Кол-во добавленных оружий
Global GunShotGunAdded
Global GunMiniGunAdded
Global GunRocketAdded

;Параметры иконки дробовика
Global GunShotGunImageSize 
Global GunShotGunImageID   
Global GunShotGunImageX  
Global GunShotGunImageY

;Параметры иконки пулемета
Global GunMinigunImageSize
Global GunMiniGunImageID   
Global GunMinigunImageX   
Global GunMiniGunImageY    

;Параметры иконки ракет
Global GunRocketImageSize  
Global GunRocketImageID   
Global GunRocketImageX   
Global GunRocketImageY    

;Кол-во оружий, выбранное, центральная точка и смещение
Global GunCount
Global GunSelected
Global GunPoint
Global GunDisplace# 

;Флаги стрельбы
Global GunFireRight
Global GunFireLeft

;Звуки оружий
Global GunShotGunSoundShot
Global GunShotGunSoundReload
Global GunMiniGunSoundClick
Global GunMiniGunSoundFire
Global GunMiniGunSoundUp
Global GunMiniGunSoundDown
Global GunMiniGunSoundRotate
Global GunRocketSoundFire

;Работа со звуком
Dim GunMiniGunChennelFire(1)
Dim GunMiniGunChennelRotate(1)
Dim GunSoundPoint(1)

;Структура оружий
Type tGun
	Field Name
	Field Entity[1]
	Field PartBody[1]
	Field PartMove[1]
	Field Status[1]
	Field ID
	Field Displace#[1]
	Field ShotTime[1]
	Field Enabled[1]
End Type

;Инициализация модул оружий
Function InitGuns()
	GunModelShotGun = xLoadAnimMesh("base\models\shotgun\shotgun.b3d")
	xHideEntity GunModelShotGun
	
	GunModelMiniGun = xLoadAnimMesh("base\models\minigun\minigun.b3d")
	xHideEntity GunModelMiniGun
	
	GunModelRocket = xLoadAnimMesh("base\models\rocket\rocket.b3d")
	xHideEntity GunModelRocket
	
	GunShotGunSoundShot   = xLoad3DSound("base\sounds\shotgun\Fire.wav")
	GunShotGunSoundReload = xLoad3DSound("base\sounds\shotgun\Reload.wav")
	GunMiniGunSoundClick  = xLoad3DSound("base\sounds\minigun\Click.wav")
	GunMiniGunSoundFire   = xLoad3DSound("base\sounds\minigun\Fire.wav"): xLoopSound GunMiniGunSoundFire
	GunMiniGunSoundUp     = xLoad3DSound("base\sounds\minigun\RotateUp.wav")
	GunMiniGunSoundDown   = xLoad3DSound("base\sounds\minigun\RotateDown.wav")
	GunMiniGunSoundRotate = xLoad3DSound("base\sounds\minigun\Rotate.wav"): xLoopSound GunMiniGunSoundRotate
	GunRocketSoundFire    = xLoad3DSound("base\sounds\rocket\fire.wav")

	GunPoint = xCreatePivot()
	GunSoundPoint(GunArmRight) = xCreatePivot(GunPoint)
	GunSoundPoint(GunArmLeft)  = xCreatePivot(GunPoint)
	xMoveEntity GunSoundPoint(GunArmRight), 2, 0, 0
	xMoveEntity GunSoundPoint(GunArmLeft), -2, 0, 0
	
	xPositionEntity GunPoint, xEntityX(PlayerCollider), 2.75, xEntityZ(PlayerCollider) + 1
	xEntityParent GunPoint, PlayerCamera
	xRotateEntity GunPoint, -90, 0, 0

	xEntityShininess GunModelShotGun, True
	xEntityShininess GunModelMiniGun, True

	GunShotGunImageSize = 32
	GunShotGunImageID   = 3
	GunShotGunImageX    = WindowWidth  - GunShotGunImageSize - 50
	GunShotGunImageY    = WindowHeight - GunShotGunImageSize - 50
	
	GunMinigunImageSize = 32
	GunMiniGunImageID   = 4
	GunMinigunImageX    = WindowWidth  - GunMinigunImageSize - 50
	GunMiniGunImageY    = WindowHeight - GunMinigunImageSize - 90
	
	GunRocketImageSize  = 32
	GunRocketImageID    = 5
	GunRocketImageX     = WindowWidth  - GunRocketImageSize - 50
	GunRocketImageY     = WindowHeight - GunRocketImageSize - 130
End Function

;Добавление оружия
Function CreateGun(GunName)
	;Определение типа добавляемого оружия
	Select GunName
		Case GunNameShotGun
			If GunShotGunAdded = 0 AddGun = True ElseIf GunShotGunAdded = 1 ActivatGun = True
			GunShotGunAdded = GunShotGunAdded + 1
			GunShotGunAmmo  = GunShotGunAmmo + 60
		Case GunNameMiniGun
			If GunMiniGunAdded = 0 AddGun = True ElseIf GunMiniGunAdded = 1 ActivatGun = True
			GunMiniGunAdded = GunMiniGunAdded + 1
			GunMiniGunAmmo = GunMiniGunAmmo + 500
		Case GunNameRocket
			If GunRocketAdded = 0  AddGun = True ElseIf GunRocketAdded = 1  ActivatGun = True
			GunRocketAdded = GunRocketAdded + 1
			GunRocketAmmo = GunRocketAmmo + 20
	End Select

	;Если необходимо загрузить новое оружие
	If AddGun
		GunCount = GunCount + 1
	
		Gun.tGun = New tGun
	
		Gun\Name = GunName
		Gun\Displace[GunArmRight] = 1.0
		Gun\Displace[GunArmLeft]  = 1.0
		Gun\ID = GunCount
		GunSelected = Gun\ID
	
		Select GunName
			Case GunNameShotGun
				Gun\Entity[GunArmRight] = xCopyMesh(GunModelShotGun, GunPoint)
				Gun\Entity[GunArmLeft]  = xCopyMesh(GunModelShotGun, GunPoint)
				
			Case GunNameMiniGun
				Gun\Entity[GunArmRight] = xCopyMesh(GunModelMiniGun, GunPoint)
				Gun\Entity[GunArmLeft]  = xCopyMesh(GunModelMiniGun, GunPoint)
				
			Case GunNameRocket
				Gun\Entity[GunArmRight] = xCopyMesh(GunModelRocket, GunPoint)
				Gun\Entity[GunArmLeft] =  xCopyMesh(GunModelRocket, GunPoint)
				
		End Select
	
		xPositionEntity Gun\Entity[GunArmRight], 0.5, 1, 0
		xRotateEntity   Gun\Entity[GunArmRight], -90* 1, 0, 0

		xPositionEntity Gun\Entity[GunArmLeft],-0.5, 1, 0
		xRotateEntity   Gun\Entity[GunArmLeft], -90* 1, 0, 0

		Gun\PartBody[GunArmRight] = xFindChild(Gun\Entity[GunArmRight], "Body")
		Gun\PartMove[GunArmRight] = xFindChild(Gun\Entity[GunArmRight], "Barrels")
	
		Gun\PartBody[GunArmLeft] = xFindChild(Gun\Entity[GunArmLeft], "Body")
		Gun\PartMove[GunArmLeft] = xFindChild(Gun\Entity[GunArmLeft], "Barrels")

		;Скрыть оружиее во второй руке
		Gun\Enabled[GunArmRight] = True
		xHideEntity Gun\Entity[GunArmLeft]

	;Иначе активировать оружее во второй руке
	ElseIf ActivatGun
		For Gun.tGun = Each tGun
			If Gun\Name = GunName
				GunSelected = Gun\ID
				Gun\Enabled[GunArmLeft] = True
				xShowEntity Gun\Entity[GunArmLeft]
			End If
		Next
	End If
End Function

;Обновление оружий
Function UpdateGuns()
	;Переключение между орижиями
	GunSelected = GunSelected + MouseSpeedZ
	If GunSelected < 1 GunSelected = GunCount
	If GunSelected > GunCount GunSelected = 1

	;Флаги стрельбы
	GunFireRight = False
    GunFireLeft  = False 

	;Рассчет смещения оружия для покачивани при ходьбе
	If PlayerMoving
		GunDisplace = CurveValue(Sin(GameMillis * 0.4) * 0.08, GunDisplace, 5)
	Else
		GunDisplace = CurveValue(0.0, GunDisplace, 10)
	End If

	;Перебор оружий
	For Gun.tGun = Each tGun

		;Левого и правого, каждого типа
		For CurrentGun = GunArmRight To GunArmLeft

			;Если оружее активно
			If Gun\Enabled[CurrentGun]

				;Если оружее выбранно и все стальные убраны 
				If Gun\ID = GunSelected And AllGunFinished()
					;Показать его
					If Gun\Status[CurrentGun] = GunStatusNone
						Gun\Status[CurrentGun] = GunStausStarting
						Gun\Displace[CurrentGun] = 1.0
					End If
				;Если оно не выбранно, убрать его
				Else
					If Gun\Status[CurrentGun] <> GunStatusFinish And Gun\Status[CurrentGun] <> GunStatusNone
							Gun\Status[CurrentGun] = GunStatusFinish
							Gun\Displace[CurrentGun] = 0.0
							xStopChannel GunMiniGunChennelFire(CurrentGun)
							xStopChannel GunMiniGunChennelRotate(CurrentGun)
					End If
				End If

				;Выборка режима оружия
				Select Gun\Status[CurrentGun]
				
					;Режим появления на экране
					Case GunStausStarting
					
						;Рассчет смещения
						If Gun\Displace[CurrentGun] > 0 Gun\Displace[CurrentGun] = Gun\Displace[CurrentGun] - 0.05

						;позиционирование правого оружия
						If CurrentGun = GunArmRight
							xPositionEntity Gun\Entity[CurrentGun], 0.5, Gun\Displace[CurrentGun], 0
							xRotateEntity   Gun\Entity[CurrentGun], -90* Gun\Displace[CurrentGun], 0, 0
						End If

						;позиционирование левого оружия
						If CurrentGun = GunArmLeft
							xPositionEntity Gun\Entity[CurrentGun],-0.5, Gun\Displace[CurrentGun], 0
							xRotateEntity   Gun\Entity[CurrentGun], -90* Gun\Displace[CurrentGun], 0, 0
						End If

						;переключиться в режим готовности
						If Gun\Displace[CurrentGun] <= 0
							Gun\Displace[CurrentGun] = 1.0
							Gun\Status[CurrentGun] = GunStatusReady
						End If

					;Режим исчезновения с экрана 
					Case GunStatusFinish
						;рассчет смещения
						If Gun\Displace[CurrentGun] < 1.0 Gun\Displace[CurrentGun] = Gun\Displace[CurrentGun] + 0.05

						;позиционирование правого оружия
						If CurrentGun = GunArmRight
							xPositionEntity Gun\Entity[CurrentGun], 0.5, Gun\Displace[CurrentGun], 0
							xRotateEntity   Gun\Entity[CurrentGun], -90* Gun\Displace[CurrentGun], 0, 0
						End If

						;позиционирование левого оружия
						If CurrentGun = GunArmLeft
							xPositionEntity Gun\Entity[CurrentGun],-0.5, Gun\Displace[CurrentGun], 0
							xRotateEntity   Gun\Entity[CurrentGun], -90* Gun\Displace[CurrentGun], 0, 0
						End If

						;Переключение в режим отключенного
						If Gun\Displace[CurrentGun] >= 1.0
							Gun\Displace[CurrentGun] = 1.0
							Gun\Status[CurrentGun] = GunStatusNone
						End If

					;Режим готовности
					Case GunStatusReady

						;Правое
						If CurrentGun = GunArmRight
							xPositionEntity Gun\Entity[CurrentGun], 0.5 + GunDisplace * 0.5, GunDisplace, GunDisplace * 0.5

							;Если нажата кнопка мыши начать стрельюу
							If MouseDownRight Or (MouseDownLeft And Gun\Enabled[GunArmLeft] = False)

								;Выборка типа оружия
								Select Gun\Name

									;Дробовик
									Case GunNameShotGun

										;Если достаточно патронов и пришло время выстрела
										If Gun\ShotTime[CurrentGun] < GameMillis And GunShotGunAmmo > 0
											GunFireRight = True
											Gun\Displace[CurrentGun] = 0.0 
											Gun\Status[CurrentGun]   = GunStatusFireUp
											Gun\ShotTime[CurrentGun] = GameMillis + GunSpeedShotGun

											;Добавление вспышки
											CreateFlash(0.5, 2.5, PlayerCamera)

											;Вычитание патронов
											GunShotGunAmmo = GunShotGunAmmo - 2

											;Звук выстрела
											xEmitSound(GunShotGunSoundShot, GunSoundPoint(CurrentGun))
										End If

									;Пулемет
									Case GunNameMiniGun
										;Если достаточно патронов
										If GunMiniGunAmmo > 0
											;Начать стрельбу
											Gun\Displace[CurrentGun] = 0.0 
											Gun\Status[CurrentGun]   = GunStatusFireUp
											;воспроизвести звук выстрела
											xEmitSound(GunMiniGunSoundUp, Gun\Entity[CurrentGun])
										;Воспроизвести звук включения
										Else
											If Gun\ShotTime[CurrentGun] < GameMillis
												xEmitSound(GunMiniGunSoundClick, Gun\Entity[CurrentGun])
												Gun\ShotTime[CurrentGun] = GameMillis + GunSpeedMiniGun
											End If
										End If

									;Ракетница
									Case GunNameRocket
										;Если пришло время выстрела и патронов достаточно
										If Gun\ShotTime[CurrentGun] < GameMillis And GunRocketAmmo > 0
											Gun\Displace[CurrentGun] = 0.0 
											Gun\Status[CurrentGun]   = GunStatusFireUp
											Gun\ShotTime[CurrentGun] = GameMillis + GunSpeedRocket

											;Вычисть 1 из кол-ва боеприпасов
											GunRocketAmmo  = GunRocketAmmo - 1

											;Спавн ракеты в координатах оружия
											CreateRocket(1.2 + GunDisplace * 0.5, 2.0)

											;Звук запуска ракеты
											xEmitSound(GunRocketSoundFire, Gun\Entity[CurrentGun])
										End If
								End Select
							End If
						End If

						;Если оружиее левое
						If CurrentGun = GunArmLeft

							;Позиционирование
							xPositionEntity Gun\Entity[CurrentGun],-0.5 + GunDisplace * 0.5,-GunDisplace,-GunDisplace * 0.5

							;Если нажата левая кнопа мыши
							If MouseDownLeft

								;Выборка оружия
								Select Gun\Name

									;Дробвик
									Case GunNameShotGun
										;Если пришло время и патронов достаточно
										If Gun\ShotTime[CurrentGun] < GameMillis And GunShotGunAmmo > 0
											GunFireLeft = True
											Gun\Displace[CurrentGun] = 0.0 
											Gun\Status[CurrentGun]   = GunStatusFireUp
											Gun\ShotTime[CurrentGun] = GameMillis + GunSpeedShotGun
											CreateFlash(-0.5, 2.5, PlayerCamera)

											;Вычесть боеприпасы
											GunShotGunAmmo = GunShotGunAmmo - 2

											;Воспроизвести звук выстрела
											xEmitSound(GunShotGunSoundShot, GunSoundPoint(CurrentGun))
										End If

									;Пулемет
									Case GunNameMiniGun
										;Если патронов достаточно начать огонь
										If GunMiniGunAmmo > 0
											Gun\Displace[CurrentGun] = 0.0 
											Gun\Status[CurrentGun]   = GunStatusFireUp
											xEmitSound(GunMiniGunSoundUp, Gun\Entity[CurrentGun])
										;Иначе только звук включения
										Else
											If Gun\ShotTime[CurrentGun] < GameMillis
												xEmitSound(GunMiniGunSoundClick, Gun\Entity[CurrentGun])
												Gun\ShotTime[CurrentGun] = GameMillis + GunSpeedMiniGun
											End If
										End If
										
									;Ракетница
									Case GunNameRocket
										;Если пришло время и боеприпасов достаточно
										If Gun\ShotTime[CurrentGun] < GameMillis And GunRocketAmmo > 0
											Gun\Displace[CurrentGun] = 0.0 
											Gun\Status[CurrentGun]   = GunStatusFireUp
											Gun\ShotTime[CurrentGun] = GameMillis + GunSpeedRocket

											;Вычесть боеприпасы
											GunRocketAmmo  = GunRocketAmmo - 1

											;Спавн ракеты в координатах оружия
											CreateRocket(-1.2 + GunDisplace * 0.5, 2.0)

											;Звук запуска ракеты
											xEmitSound(GunRocketSoundFire, Gun\Entity[CurrentGun])
										End If
								End Select
							End If
						End If

					;Анимация при выстреле
					Case GunStatusFireUp
					
						;рассчет смещения
						If Gun\Displace[CurrentGun] < 1.0 Gun\Displace[CurrentGun] = Gun\Displace[CurrentGun] + 0.1

						;Выборка оружий
						Select Gun\Name
						
							;Дробовик
							Case GunNameShotGun

								;Позиционирование правого оружия
								If CurrentGun = GunArmRight
									xPositionEntity Gun\Entity[CurrentGun], 0.5 + GunDisplace * 0.5, GunDisplace + Gun\Displace[CurrentGun], GunDisplace * 0.5
									xRotateEntity   Gun\Entity[CurrentGun], -30* Gun\Displace[CurrentGun], 0, 0
								End If

								;Позиционирование левого оружия
								If CurrentGun = GunArmLeft
									xPositionEntity Gun\Entity[CurrentGun],-0.5 + GunDisplace * 0.5,-GunDisplace  + Gun\Displace[CurrentGun],-GunDisplace * 0.5
									xRotateEntity   Gun\Entity[CurrentGun], -30* Gun\Displace[CurrentGun], 0, 0
								End If

							;Пулемет
							Case GunNameMiniGun

								;Позиционирование Правого оружия
								If CurrentGun = GunArmRight
									xPositionEntity Gun\Entity[CurrentGun], 0.5 + GunDisplace * 0.5, GunDisplace + Gun\Displace[CurrentGun] * 0.2, GunDisplace * 0.5
									xRotateEntity   Gun\PartMove[CurrentGun], 0, 180* Gun\Displace[CurrentGun], 0
								End If

								;Позиционирование левого оружия
								If CurrentGun = GunArmLeft
									xPositionEntity Gun\Entity[CurrentGun],-0.5 + GunDisplace * 0.5,-GunDisplace  + Gun\Displace[CurrentGun] * 0.2,-GunDisplace * 0.5
									xRotateEntity   Gun\PartMove[CurrentGun], 0,-180* Gun\Displace[CurrentGun], 0
								End If

							;Ракетница
							Case GunNameRocket

								;позиционирование правого оружия
								If CurrentGun = GunArmRight
									xPositionEntity Gun\Entity[CurrentGun], 0.5 + GunDisplace * 0.5, GunDisplace + Gun\Displace[CurrentGun] * 0.2, GunDisplace * 0.5
									xRotateEntity   Gun\PartMove[CurrentGun], 0, 45* Gun\Displace[CurrentGun], 0
								End If

								;позиционирование левого оружия
								If CurrentGun = GunArmLeft
									xPositionEntity Gun\Entity[CurrentGun],-0.5 + GunDisplace * 0.5,-GunDisplace  + Gun\Displace[CurrentGun] * 0.2,-GunDisplace * 0.5
									xRotateEntity   Gun\PartMove[CurrentGun], 0,-45* Gun\Displace[CurrentGun], 0
								End If
						End Select

						;При завершении анимации
						If Gun\Displace[CurrentGun] >= 1.0

							;Выборка оружий
							Select Gun\Name

								;Дробовик
								Case GunNameShotGun
									;переключить в анимацию после выстрела
									Gun\Displace[CurrentGun] = 1.0
									Gun\Status[CurrentGun] = GunStatusFireDown

								;Пулемет
								Case GunNameMiniGun

									;Левый
									If CurrentGun = GunArmLeft
									
										;Переключение в режим стрельбы
										If MouseDownLeft
											GunMiniGunChennelFire(CurrentGun) = xEmitSound(GunMiniGunSoundFire, Gun\Entity[CurrentGun])
											GunMiniGunChennelRotate(CurrentGun) = xEmitSound(GunMiniGunSoundRotate, Gun\Entity[CurrentGun])
											Gun\Displace[CurrentGun] = 1.0
											Gun\Status[CurrentGun] = GunStatusFireing
										;Переключение в режим после стрельбы
										Else
											Gun\Displace[CurrentGun] = 1.0
											Gun\Status[CurrentGun] = GunStatusFireDown
										End If
										
									;Правый
									Else
									
										;Переключение в режим стрельбы
										If MouseDownRight Or (MouseDownLeft And Gun\Enabled[GunArmLeft] = False)
											GunMiniGunChennelFire(CurrentGun) = xEmitSound(GunMiniGunSoundFire, Gun\Entity[CurrentGun])
											GunMiniGunChennelRotate(CurrentGun) = xEmitSound(GunMiniGunSoundRotate, Gun\Entity[CurrentGun])
											Gun\Displace[CurrentGun] = 1.0
											Gun\ShotTime[CurrentGun] = 0.0
											Gun\Status[CurrentGun] = GunStatusFireing
										;Иначе в режим после стрельбы
										Else
											Gun\Displace[CurrentGun] = 1.0
											Gun\Status[CurrentGun] = GunStatusFireDown
										End If
									End If
									
								;Ракетница
								Case GunNameRocket

									;Переключение в режим огня
									Gun\Displace[CurrentGun] = 1.0
									Gun\Status[CurrentGun] = GunStatusFireing
							End Select
						End If

					;Режим после огня
					Case GunStatusFireDown
					
						;рассчет смещения
						If Gun\Displace[CurrentGun] > 0.0 Gun\Displace[CurrentGun] = Gun\Displace[CurrentGun] - 0.1

						;Выборка оружия
						Select Gun\Name

							;Дробовик
							Case GunNameShotGun

								;Позиционирование правого
								If CurrentGun = GunArmRight
									xPositionEntity Gun\Entity[CurrentGun], 0.5 + GunDisplace * 0.5, GunDisplace + Gun\Displace[CurrentGun], GunDisplace * 0.5
									xRotateEntity   Gun\Entity[CurrentGun], -30* Gun\Displace[CurrentGun], 0, 0
								End If

								;Позиционирование левого
								If CurrentGun = GunArmLeft
									xPositionEntity Gun\Entity[CurrentGun],-0.5 + GunDisplace * 0.5,-GunDisplace  + Gun\Displace[CurrentGun],-GunDisplace * 0.5
									xRotateEntity   Gun\Entity[CurrentGun], -30* Gun\Displace[CurrentGun], 0, 0
								End If

							;Пулемет
							Case GunNameMiniGun

								;Позиционирование правого
								If CurrentGun = GunArmRight
									xPositionEntity Gun\Entity[CurrentGun], 0.5 + GunDisplace * 0.5, GunDisplace + Gun\Displace[CurrentGun] * 0.2, GunDisplace * 0.5
									xRotateEntity   Gun\PartMove[CurrentGun], 0, 180* (1 - Gun\Displace[CurrentGun]), 0
								End If

								;Позиционирование левого
								If CurrentGun = GunArmLeft
									xRotateEntity   Gun\PartMove[CurrentGun], 0,-180* (1 - Gun\Displace[CurrentGun]), 0
									xPositionEntity Gun\Entity[CurrentGun],-0.5 + GunDisplace * 0.5,-GunDisplace  + Gun\Displace[CurrentGun] * 0.2,-GunDisplace * 0.5
								End If

							;Ракетница
							Case GunNameRocket

								;Позиционирование Правой
								If CurrentGun = GunArmRight
									xPositionEntity Gun\Entity[CurrentGun], 0.5 + GunDisplace * 0.5, GunDisplace + Gun\Displace[CurrentGun] * 0.2, GunDisplace * 0.5
									xRotateEntity   Gun\PartMove[CurrentGun], 0, 45 + 45* (1 -Gun\Displace[CurrentGun]), 0
								End If

								;Позиционирование левой
								If CurrentGun = GunArmLeft
									xPositionEntity Gun\Entity[CurrentGun],-0.5 + GunDisplace * 0.5,-GunDisplace  + Gun\Displace[CurrentGun] * 0.2,-GunDisplace * 0.5
									xRotateEntity   Gun\PartMove[CurrentGun], 0, 45 -45* (1 - Gun\Displace[CurrentGun]), 0
								End If
						End Select

						;При зиавршении переключиться в режим готовности
						If Gun\Displace[CurrentGun] <= 0.0
							Gun\Displace[CurrentGun] = 0.0
							Gun\Status[CurrentGun] = GunStatusReady

							;Выборка оружий
							Select Gun\Name
							
								;Дробовик
								Case GunNameShotGun
								
									;Воспроизвести звук перезарядки
									xEmitSound(GunShotGunSoundReload,  GunSoundPoint(CurrentGun))

								;Пулемет
								Case GunNameMiniGun

									;Остановка звуков вращения и стрельбы
									xStopChannel GunMiniGunChennelFire(CurrentGun)
									xStopChannel GunMiniGunChennelRotate(CurrentGun)
							End Select
						End If

					;Режим стрельбы
					Case GunStatusFireing

						;Выборка оружия
						Select Gun\Name
							;Пулемет
							Case GunNameMiniGun

								;Правый
								If CurrentGun = GunArmRight

									;Если кнопка отжата закончить стрельбу
									If Gun\Enabled[GunArmLeft] = False
										If (MouseDownRight = False And MouseDownLeft = False) Or GunMiniGunAmmo = 0 Gun\Status[CurrentGun] =  GunStatusFireDown Gun\Displace[CurrentGun] = 1.0 xEmitSound(GunMiniGunSoundDown, Gun\Entity[CurrentGun])
									Else
										If MouseDownRight = False Or GunMiniGunAmmo = 0 Gun\Status[CurrentGun] =  GunStatusFireDown Gun\Displace[CurrentGun] = 1.0 xEmitSound(GunMiniGunSoundDown, Gun\Entity[CurrentGun])
									End If

									;Позиционирование
									xPositionEntity Gun\Entity[CurrentGun], 0.5 + GunDisplace * 0.5, GunDisplace + Gun\Displace[CurrentGun] * 0.2 - Rnd(-0.025, 0.025), GunDisplace * 0.5
									xTurnEntity   Gun\PartMove[CurrentGun], 0, 20, 0

									;Вспышка
									CreateFlash(0.5 + GunDisplace * 0.5, 2.75, PlayerCamera)

									;Включить флаг стрельбы если пришло время
									If Gun\ShotTime[CurrentGun] < GameMillis And GunFireLeft = False And GunMiniGunAmmo > 0
										GunFireRight = True
										Gun\ShotTime[CurrentGun] = Gun\ShotTime[CurrentGun] + GunSpeedMiniGu
									End If
								End If

								;Левый
								If CurrentGun = GunArmLeft
								
									;Если кнопка отжата закончить стрельбу
									If MouseDownLeft = False Or GunMiniGunAmmo = 0 Gun\Status[CurrentGun] =  GunStatusFireDown Gun\Displace[CurrentGun] = 1.0 xEmitSound(GunMiniGunSoundDown, Gun\Entity[CurrentGun])

									;Позиционирование
									xPositionEntity Gun\Entity[CurrentGun],-0.5 + GunDisplace * 0.5,-GunDisplace + Gun\Displace[CurrentGun] * 0.2  - Rnd(-0.025, 0.025),-GunDisplace * 0.5
									xTurnEntity   Gun\PartMove[CurrentGun], 0,-20, 0

									;Вспышка
									CreateFlash(-0.5 + GunDisplace * 0.5, 2.75, PlayerCamera)

									;Флаг стрельбы если настало время
									If Gun\ShotTime[CurrentGun] < GameMillis And GunMiniGunAmmo > 0
										GunFireLeft = True
										Gun\ShotTime[CurrentGun] = Gun\ShotTime[CurrentGun] + GunSpeedMiniGun
									End If
	
								End If

								;Вычетание патронов (раз в кадр для реалистичности)
								If GunFireLeft And  GunMiniGunAmmo GunMiniGunAmmo = GunMiniGunAmmo - 1
								If GunFireRight And GunMiniGunAmmo GunMiniGunAmmo = GunMiniGunAmmo - 1

							;Ракетница
							Case GunNameRocket

								;Включить режим после стрельбы
								Gun\Status[CurrentGun]   =  GunStatusFireDown
								Gun\Displace[CurrentGun] = 1.0							
						End Select
				End Select
			End If
		Next
	Next
End Function

;Проверка на активированность оружий
Function AllGunFinished()
	For Gun.tGun = Each tGun
		If Gun\ID <> GunSelected
			If Gun\Status[0] <> GunStatusNone Return False
			If Gun\Status[1] <> GunStatusNone Return False
		End If
	Next

	Return True
End Function

;Сброс всех оружий
Function ResetGuns()
	For Gun.tGun = Each tGun
		xFreeEntity Gun\Entity[0]
		xFreeEntity Gun\Entity[1]
		Delete Gun
	Next

	GunShotGunAmmo = 0
	GunMiniGunAmmo = 0
	GunRocketAmmo  = 0
	
	GunShotGunAdded = 0
	GunMiniGunAdded = 0
	GunRocketAdded  = 0
	
	GunCount    = 0
	GunSelected = 0
	GunDisplace = 0
End Function
