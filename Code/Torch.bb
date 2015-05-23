;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль факелов
;==================================================================

;Параметры текстуры факелов
Const TorchFireFrameSizeX = 100
Const TorchFireFrameSizeY = 250
Const TorchFireFrameCount = 10
Const TorchFireFrameSpeed = 50

;Тестура факелы и огня
Global TorchTexture
Global TorchFireTexture

;Текстура факела
Global TorchLight

;Структура факела
Type tTorchFire
	Field Entity
	Field FrameTime
	Field FrameID
End Type

;Инициализация модуля факелов
Function InitTorch()
	TorchTexture     = xLoadTexture("base\textures\Shadow.png", FLAGS_ALPHA)
	TorchFireTexture = xLoadAnimTexture("base\textures\fire.jpg", FLAGS_ALPHA, TorchFireFrameSizeX, TorchFireFrameSizeY, 0, TorchFireFrameCount)
End Function

;Добавление факела
Function CreateTorch(Entity)
	Torch = CreateTorchMesh()
	xEntityTexture  Torch, TorchTexture
	xScaleEntity    Torch, 20, 20, 20
	xPositionEntity Torch, xEntityX(Entity, 1), xEntityY(Entity, 1), xEntityZ(Entity, 1)
	xRotateEntity   Torch, 90, xEntityYaw(Entity, 1) + 90, 90, 1
	xMoveEntity     Torch, 0, -0.75, 0
	xEntityFX       Torch, FX_FULLBRIGHT

	TorchFire.tTorchFire = New tTorchFire

	TorchFire\Entity = xCreateSprite()
	TorchFire\FrameID = Rand(0, TorchFireFrameCount - 1)
	xSpriteViewMode TorchFire\Entity, SPRITE_FIXEDYAW
	xEntityTexture  TorchFire\Entity, TorchFireTexture, TorchFire\FrameID
	xScaleSprite    TorchFire\Entity, 1.5, 3.0
	xPositionEntity TorchFire\Entity, xEntityX(Entity, 1), xEntityY(Entity, 1) + 2.0, xEntityZ(Entity, 1)
	xEntityFX       TorchFire\Entity, FX_FULLBRIGHT
End Function

;Обновление факелов
Function UpdateTorches()
	For TorchFire.tTorchFire = Each tTorchFire
		If TorchFire\FrameTime < GameMillis
			TorchFire\FrameID = TorchFire\FrameID + 1
			If TorchFire\FrameID = TorchFireFrameCount TorchFire\FrameID = 0
			xEntityTexture  TorchFire\Entity, TorchFireTexture, TorchFire\FrameID
			TorchFire\FrameTime = GameMillis + TorchFireFrameSpeed
		End If
	Next
End Function

;Генерация геометрии для факела
Function CreateTorchMesh()
	; Creation mesh:
	Mesh = xCreateMesh()
	Surface = xCreateSurface(mesh)

	; Vertex adding:
	v1 = xAddVertex(Surface, 0.5, 0, 0.5, 0, 1)
	v2 = xAddVertex(Surface, 0.5, 0,-0.5, 0, 0)
	v3 = xAddVertex(Surface,-0.5, 0,-0.5, 1, 0)
	v4 = xAddVertex(Surface,-0.5, 0, 0.5, 1, 1)

	; Triangle adding:
	tri1 = xAddTriangle(Surface, v1, v2, v4)
	tri2 = xAddTriangle(Surface, v3, v4, v2)

	; Return updated mesh:
	xUpdateNormals Mesh
	Return Mesh 
End Function
