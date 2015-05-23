;==================================================================
;Project Title:    BloodCore 
;Author:     	     (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль уровн
;==================================================================

;Тип коллизии
Const CollType_Level  = 1

;Уровень, свет и небо
Global LevelEntity
Global LevelLight
Global LevelSpace

;Текстура неба
Global LevelTextureSpace

;Инициализация модуля уровня
Function InitLevel()
	LevelTextureSpace = xLoadTexture("base\textures\Space.jpg")
	LevelEntity = xLoadAnimMesh("base\level\arena.b3d")
	LevelLight  = xCreateLight(LIGHT_DIRECTIONAL)
	xScaleEntity LevelEntity, 0.5, 0.5, 0.5
	xLightColor  LevelLight, 255, 196, 108

	ReadLevel(LevelEntity)
	xAmbientLight 121, 98, 59

	LevelSpace = xCreateSphere()
	xPositionEntity LevelSpace, 0, PlayerHeight, 0
	xEntityParent   LevelSpace, PlayerCollider
	xEntityOrder    LevelSpace, 1
	xScaleEntity    LevelSpace, -2, -2, -2
	xEntityTexture  LevelSpace, LevelTextureSpace
	xEntityFX       LevelSpace, FX_FULLBRIGHT
End Function

;Обновление уровня
Function UpdateLevel()
	xRotateEntity LevelSpace, 80, 0, 0, 1
End Function

;Сброс уровня
Function ResetLevel()
	xRotateEntity LevelSpace, 80, 0, 0, 1
End Function

;Рекурсивный перебор уровня
Function ReadLevel(Entity)
	Local Name$ = Lower(xEntityName(Entity))
	
	Select Name
	
		Case "spawner"	
			CreateSpawner(Entity)
			
		Case "door"
			CreateDoor(Entity)
			
		Case "wall"
			xEntityType     Entity, CollType_Level
			xEntityPickMode Entity, 0
			xEntityAlpha    Entity, 0.0
			
		Case "fakel"
			xTranslateEntity Entity, 0, -8, 0
			
		Case "static"
			xEntityType     Entity, CollType_Level
			
		Default
			xEntityType     Entity, CollType_Level
			xEntityPickMode Entity, PICK_TRIMESH
	End Select

	If Instr(Name, "fire") CreateTorch(Entity)

	For i = 0 To xCountChildren(Entity) - 1 
		ReadLevel(xGetChild(entity, i))
	Next
End Function
