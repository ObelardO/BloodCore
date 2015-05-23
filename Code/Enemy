;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль врагов
;==================================================================

;Тип столкновений для врагов
Const CollType_Enemy  = 3

;Режимы поведения врагов
Const EnemyStatusRun    = 0
Const EnemyStatusFire   = 1
Const EnemyStatusReload = 2
Const EnemyStatusDie    = 3
Const EnemyStatusEscape = 4

;Анимации врагов
Const EnemyAnimRun    = 1
Const EnemyAnimFire   = 2
Const EnemyAnimReload = 3
Const EnemyAnimDie1   = 4
Const EnemyAnimDie2   = 5
Const EnemyAnimDie3   = 6

;Максимальное кол-во врагов на экране
Global GameMaxEnemy   = 30
;Скорость врагов
Global EnemySpeed#    = 0.5
;Множитель для увелечения кол-ва в волнах
Const GameFactorEnemy = 10

;Основные параметры врагов
Const EnemyHealth     = 1
Const EnemyMoveTime   = 1000
Const EnemyDiedTime   = 6000
Const EnemyShotTime   = 500
Const EnemyReloadtime = 300
Const EnemyShotCount  = 5
Const EnemyDamage     = 2

;Модель врага
Global ModelEnemy

;Звуки врагов
Dim EnemySoundDie(1)
Global EnemySoundFire
Global EnemySoundReload
Global EnemySoundRicochet

;Параметры для рассчета комбо
Const  EnemyForComboTime = 1000
Global EnemyLastDieTime

;Шейдер
Global EnemyForce = True
Global EnemyShader

;Структура для врага
Type tEnemy
	Field Health
	Field Collider
	Field Status
	Field ShotCount
	Field ShotTime
	Field MoveTime
	Field DiedTime
	Field Point
	Field Died
	Field Alpha#
End Type

;Инициализация модуля врагов
Function InitEnemy()
	;Загрузка ресурсов
	ModelEnemy = xLoadAnimMesh("base\models\enemy\Enemy.B3D")
	EnemySoundDie(0) = xLoad3DSound("base\sounds\enemy\Die1.wav")
	EnemySoundDie(1) = xLoad3DSound("base\sounds\enemy\Die2.wav")
	EnemySoundFire   = xLoad3DSound("base\sounds\enemy\Fire.wav")
	EnemySoundRicochet = xLoad3DSound("base\sounds\enemy\ricochet.wav")
	xMoveEntity ModelEnemy, 0, 1000, 0
	xHideEntity ModelEnemy

	;Загрузка анимаций
	xExtractAnimSeq ModelEnemy, 118, 134; EnemyAnimRun
	xExtractAnimSeq ModelEnemy, 150, 159; EnemyAnimFire
	xExtractAnimSeq ModelEnemy, 214, 239; EnemyAnimReload
	xExtractAnimSeq ModelEnemy, 240, 270; EnemyAnimDie1
	xExtractAnimSeq ModelEnemy, 271, 311; EnemyAnimDie2
	xExtractAnimSeq ModelEnemy, 312, 325; EnemyAnimDie3

	;Применение шейдера
	If xGetMaxVertexShaderVersion() > -1 And EnemyForce
		xSetSkinningMethod SKIN_HARDWARE
		EnemyShader = xLoadFXFile("Base\shaders\skinning.fx")
		DebugLog "skining hardware"
	Else
		xSetSkinningMethod SKIN_SOFTWARE
	EndIf

	;Загрузка сразу максимального количества врагов
	;так в игре не будет подзагрузок между волнами
	For i = 1 To GameMaxEnemy
		SpawnEnemy()
	Next

	;скрыть всех врагов после загрузки
	HideAllEnemy()
End Function

;Подгрузка нужного кол-ва врагов
Function SpawnEnemys(EnemyCount = 1)
	If GameStatus = GameStatusBattle
		For i = 1 To EnemyCount
			If GameVaweAtTim < GameMaxEnemy
				SpawnerCurrentID = SpawnerCurrentID + 1
				If SpawnerCurrentID > SpanwerCount SpawnerCurrentID = 1

				;Перебор спавнеров врагов
				For Spawner.tSpawner = Each tSpawner

					;Определение готовности спавнера
					If xEntityDistance(PlayerCollider, Spawner\Entity) < 30
						Spawner\Enable = False
					Else
						Spawner\Enable = True
					End If

					;При благоприятных условиях произвести спавн
					If Spawner\Enable And Spawner\ID = SpawnerCurrentID And Spawner\Time < GameMillis
						EnemyX# = xEntityX(Spawner\Entity, 1)
						EnemyZ# = xEntityZ(Spawner\Entity, 1)
						SpawnEnemy(EnemyX, EnemyZ)
						GameVaweEnemy = GameVaweEnemy + 1
						Spawner\Time  = GameMillis + SpawnerTime
						GameVaweAtTim = GameVaweAtTim + 1
					End If
				Next
			End If
			
		Next
	End If
End Function

;Обновление врагов
Function UpdateEnemys()
	;Определение глобальных координат прицела в 3д пространстве
	CrossX#  = xEntityX(PlayerCrossPoint, True) - PlayerX
	CrossY#  = xEntityY(PlayerCrossPoint, True) - PlayerHeight
	CrossZ#  = xEntityZ(PlayerCrossPoint, True) - PlayerZ

	;Проброс луча, быстрее чем с camerapick
	xLinePick(PlayerX, PlayerHeight, PlayerZ, CrossX, CrossY, CrossZ, 100)

	;Перебор врагов
	For Enemy.tEnemy = Each tEnemy

		;Если враг жив
		If Enemy\Died = False
			;Перебор режимов
			Select Enemy\Status
				;Перебежка
				Case EnemyStatusRun
					;Если точка назначения далеко
					If xEntityDistance(Enemy\Collider, Enemy\Point) > 10
						xPointEntity  Enemy\Collider, Enemy\Point
						xMoveEntity   Enemy\Collider, 0, 0, EnemySpeed
						xRotateEntity Enemy\Collider, 0, xEntityYaw(Enemy\Collider) + 180, 0
					;Если добежал, начать стрельбу
					Else
						Enemy\Status = EnemyStatusFire
						xAnimate Enemy\Collider, ANIMATION_LOOP, 0.3, EnemyAnimFire
					End If

				;Стрельба
				Case EnemyStatusFire
					;Если настао время выстрела
					If Enemy\MoveTime < GameMillis

						;Если игрок далеко для выстрела
						If xEntityDistance(Enemy\Point, PlayerCollider) > 30

							;Определить новую точку назначения
							If Rand(0, 1) Minus = -1 Else Minus = 1

							xPositionEntity Enemy\Point, xEntityX(PlayerCollider), 0, xEntityZ(PlayerCollider)
							xTurnEntity     Enemy\Point, 0, xEntityYaw(PlayerCollider, 1) + Rand(0, 60) * Minus, 0
							xMoveEntity     Enemy\Point, 0, 0, Rand(20, 35)

							;Чтобы враг не убегал в пустоту
							While xLinePick(xEntityX(Enemy\Point), 2.0, xEntityZ(Enemy\Point), 0, -4, 0) = 0
								xPositionEntity Enemy\Point, xEntityX(PlayerCollider), 0, xEntityZ(PlayerCollider)
								xTurnEntity     Enemy\Point, 0, xEntityYaw(PlayerCollider, 1) + Rand(0, 60) * Minus, 0
								xMoveEntity     Enemy\Point, 0, 0, Rand(20, 35)
							Wend 

							;Переключить в режим бега
							Enemy\Status = EnemyStatusRun
							xAnimate Enemy\Collider, ANIMATION_LOOP, 1.0, EnemyAnimRun
						;Если игрока достаточно близко для выстрела
						Else
							;Сделать брызги крови, звук рикошета или эффект кровавого экрана
							If Rand(1, GameVaweAtTim * 1) = 1 CreateBloodPatricle(PlayerX, PlayerHeight, PlayerZ)
							If Rand(1, GameVaweAtTim * 4) = 1 xEmitSound(EnemySoundRicochet, PlayerCamera)
							If Rand(1, GameVaweAtTim * 4) = 1 PlayerEffectBlood = 1.0

							;Вспышка и звук выстрела
							CreateFlash(-6, -30, Enemy\Collider, 60)
							xEmitSound(EnemySoundFire, Enemy\Collider)

							;Если у игрока есть броня
							If PlayerArmo > 0
								;Вычесть пол урона и броню
								PlayerHealth = PlayerHealth - EnemyDamage * 0.5
								PlayerArmo = PlayerArmo - EnemyDamage
							;Иначе
							Else
								;Вычесть весь урон
								PlayerHealth = PlayerHealth - EnemyDamage
							End If

						End If

						;Запомнить время
						Enemy\MoveTime = GameMillis + EnemyMoveTime
					End If

					;Направить взгляд врага к игроку
					xPointEntity  Enemy\Collider, PlayerCollider
					xRotateEntity Enemy\Collider, 0, xEntityYaw(Enemy\Collider) + 180, 0

					;Если наступило время выстрела
					If Enemy\ShotTime < GameMillis
						;Счетчик выстрелов
						Enemy\ShotCount = Enemy\ShotCount + 1

						;Перезарядка
						If Enemy\ShotCount = EnemyShotCount
							Enemy\ShotCount = 0
							Enemy\Status = EnemyStatusReload
							xAnimate Enemy\Collider, ANIMATION_ONE, 1.0, EnemyAnimReload
						End If

						;Запомнить время выстрела
						Enemy\ShotTime = GameMillis + EnemyShotTime
					End If

				;Перезарядка
				Case EnemyStatusReload

					;Начать огонь после перезарядки
					If Enemy\ShotTime + EnemyReloadtime < GameMillis 
						Enemy\Status = EnemyStatusFire
						xAnimate Enemy\Collider, ANIMATION_LOOP, 0.3, EnemyAnimFire
					End If

				;Смерть
				Case EnemyStatusDie

					;При наступлении смерти перевести в режим исчезновения
					If Enemy\DiedTime < GameMillis
						xAnimate Enemy\Collider, ANIMATION_STOP
						Enemy\Alpha  = 1.0
						Enemy\Status = EnemyStatusEscape
					End If

				;Исчезновение
				Case EnemyStatusEscape
					;Для плавного исчезновения
					Enemy\Alpha = Enemy\Alpha - 0.1
					xEntityAlpha Enemy\Collider, Enemy\Alpha

					;При достижении полной прозрачности
					If Enemy\Alpha <= 0.0
						;Обнулить параметры, поместить объект подалье
						xHideEntity     Enemy\Collider
						xPositionEntity Enemy\Collider, 0, 100, 0
						xMoveEntity     Enemy\Point, 0, 100, 0
						xEntityAlpha    Enemy\Collider, 1.0
						;Флаг смерти
						Enemy\Died    = True
						;Переменные для определения завершения волны
						GameVaweDied  = GameVaweDied  + 1
						GameVaweAtTim = GameVaweAtTim - 1
						;Проверка на завершение этапа или всей волны
						CheckForNewStep()
					End If
			End Select

			;Проверка, подорвался ли враг
			EnemyExplosed = False
			If Enemy\Status < EnemyStatusDie
				;Если враг слишком близко к взрыву
				For Explosion.tExplosion = Each tExplosion
					If xEntityDistance(Enemy\Collider, Explosion\Entity) < ExplosionRadius
						Enemy\Health = 1
						EnemyExplosed = True
					End If
				Next

				;Если слишком близко к ракете
				For Rocket.tRocket = Each tRocket
					If xEntityDistance(Enemy\Collider, Rocket\Entity) < RocketRadius
						Rocket\BurnTime = GameMillis
					End If
				Next
			End If

			;Проверка живых врагов при стрельбе или подрыве
			If (GunFireLeft Or GunFireRight) And Enemy\Status < EnemyStatusDie Or EnemyExplosed
				;Если враг в прицеле
				If Enemy\Collider = xPickedEntity()

					;Если последнеяя жизнь
					If Enemy\Health = 1
						;Режим смерти
						Enemy\Status   = EnemyStatusDie
						Enemy\DiedTime = GameMillis + EnemyDiedTime

						;Добавление пятна крови и включение анимации смерти
						CreateBlood(xEntityX(Enemy\Collider), xEntityZ(Enemy\Collider), EnemyExplosed + 1)
						xAnimate Enemy\Collider, ANIMATION_ONE, 1.0, Rand(EnemyAnimDie1, EnemyAnimDie3)

						;Воспроизведение звука смерти врага
						xEmitSound(EnemySoundDie(Rand(0, 1)), Enemy\Point)

						;Увеличение счетчика комбо
						PlayerCombo = PlayerCombo + 1
						EnemyLastDieTime = GameMillis + EnemyForComboTime

						;Увеличение игрового счета
						PlayerScore = PlayerScore + (GunMaxCount + 1 - GunSelected)
					Else
						Enemy\Health = Enemy\Health - 1
					End If

					;Добавление частиц крови
					If EnemyExplosed
						CreateBloodPatricle(xEntityX(Enemy\Collider), 2.0, xEntityZ(Enemy\Collider))
					Else
						CreateBloodPatricle(xPickedX(), xPickedY(), xPickedZ())
					End If
				End If
			End If
		End If
	Next

	;При совершенного комбинаци обновить таймер, добавить счет
	If PlayerCombo
		If EnemyLastDieTime <= GameMillis
			PlayerScore = PlayerScore + (GunMaxCount + 1 - GunSelected) * PlayerCombo * 2
			PlayerCombo = 0
		End If
	End If
End Function

;Спавно врага
Function SpawnEnemy(EnemyX# = 0, EnemyZ# = 0)
	;Активация мертвого врага
	For Enemy.tEnemy = Each tEnemy
		If Enemy\Died = True
			Enemy\Died   = False
			Enemy\Health = EnemyHealth
			Enemy\Status = EnemyStatusRun
			xPositionEntity Enemy\Collider, EnemyX, 0.0, EnemyZ
			xMoveEntity     Enemy\Collider, Rnd(-2.0,-0.8), 0, Rnd(-2.0,-0.8)
			xPositionEntity Enemy\Point, EnemyX, 0, EnemyZ
			xShowEntity     Enemy\Collider
			Return False
		End If
	Next

	;В ином случае добавление нового
	Enemy.tEnemy = New tEnemy
	Enemy\Health = EnemyHealth

	Enemy\Collider = xCopyEntity(ModelEnemy)
	xScaleEntity Enemy\Collider, 0.05, 0.05, 0.05

	Enemy\Point = xCreatePivot()
	xPositionEntity Enemy\Point, EnemyX, 0, EnemyZ

	xPositionEntity Enemy\Collider, EnemyX, 0.0, EnemyZ
	xMoveEntity     Enemy\Collider, Rnd(-2.0,-0.8), 0, Rnd(-2.0,-0.8)
	xEntityPickMode Enemy\Collider, PICK_TRIMESH
	xEntityRadius   Enemy\Collider, 100

	;Добавление шейдера
	If xGetMaxVertexShaderVersion() > -1 And EnemyForce
		xSetEntityEffect    Enemy\Collider, EnemyShader
		xSetBonesArrayName  Enemy\Collider, "bonesMatrixArray"
		xSetEffectTechnique Enemy\Collider, "Skinned"
	EndIf

	;Анимирование (бег по умолчанию)
	xAnimate Enemy\Collider, ANIMATION_LOOP, 1.0, EnemyAnimRun
End Function

;Скрыть всех врагов
Function HideAllEnemy()
	For Enemy.tEnemy = Each tEnemy
		Enemy\Died = True
		Enemy\Status = EnemyStatusRun
		Enemy\ShotCount = 0
		Enemy\MoveTime  = 0
		Enemy\DiedTime  = 0
		Enemy\Alpha = 0.0
		xHideEntity Enemy\Collider
	Next
End Function

;Сброс всего модуля
Function ResetEmenys()
	HideAllEnemy()
End Function
