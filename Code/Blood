;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль эффектов крови
;==================================================================

;Константы для работы с пятнами крови
Const BloodTextureCount = 3
Const BloodLiveTime = 12000

;Константы для работы с частицами крови
Const BloodParticleTextureCount = 3
Const BloodParticleTextureFrameCount = 15
Const BloodParticleTextureFrameSize = 128
Const BloodParticleLiveTime = 20

;Массивы текстур для разнообразия
Dim BloodTexture(BloodTextureCount)
Dim BloodPatricleTexture(BloodParticleTextureCount)

;Структура для частиц крови
Type tBloodParticle
	Field Entity
	Field LiveTime
	Field AnimFrame
	Field TextureID
End Type

;Структура для пятен крови
Type tBlood
	Field Entity
	Field LiveTime
	Field Escaping
	Field Alpha#
End Type

;Инициализация модуля эффектов крови
Function InitBlood()
	For i = 1 To BloodTextureCount
		BloodTexture(i) = xLoadTexture("base\textures\bloodtexture" + i + ".png", FLAGS_ALPHA)
	Next

	For i = 1 To BloodParticleTextureCount
		BloodPatricleTexture(i) = xLoadAnimTexture("base\textures\bloodparticletexture" + i + ".png", FLAGS_ALPHA, BloodParticleTextureFrameSize, BloodParticleTextureFrameSize, 0, BloodParticleTextureFrameCount)
	Next
End Function

;Добавление новой частицы крови
Function CreateBloodPatricle(BloodX# = 0, BloodY# = 0, BloodZ# = 0)
	Local Size# = Rnd(2, 4)

	BloodParticle.tBloodParticle = New tBloodParticle
	BloodParticle\TextureID = Rand(1, BloodParticleTextureCount)

	BloodParticle\Entity = xCreateSprite()
	xScaleSprite    BloodParticle\Entity, Size, Size
	xSpriteViewMode BloodParticle\Entity, SPRITE_FIXED
	xEntityTexture  BloodParticle\Entity, BloodPatricleTexture(BloodParticle\TextureID)
	xPositionEntity BloodParticle\Entity, BloodX, BloodY - 1.0, BloodZ
End Function

;Добавление пятная крови
Function CreateBlood(BloodX# = 0, BloodZ# = 0, SizeFactor# = 1.0)
	Local Size# = Rnd(6, 10) * SizeFactor

	Blood.tBlood = New tBlood

	Blood\LiveTime = GameMillis + BloodLiveTime

	Blood\Entity = CreateBloodMesh()
	xEntityTexture  Blood\Entity, BloodTexture(Rand(1, BloodTextureCount))
	xScaleEntity    Blood\Entity, Size, 1, Size
	xPositionEntity Blood\Entity, BloodX, 0.05, BloodZ
	xRotateEntity   Blood\Entity, 0, Rand(360), 0
End Function

;Обновления модуля эффектов крови
Function UpdateBlood()
	For BloodParticle.tBloodParticle = Each tBloodParticle
		If BloodParticle\LiveTime < GameMillis
			BloodParticle\LiveTime = GameMillis + BloodParticleLiveTime
		
			If BloodParticle\AnimFrame = BloodParticleTextureFrameCount
				xFreeEntity BloodParticle\Entity
				Delete BloodParticle
			Else
				xEntityTexture BloodParticle\Entity, BloodPatricleTexture(BloodParticle\TextureID), BloodParticle\AnimFrame
				BloodParticle\AnimFrame = BloodParticle\AnimFrame + 1
			End If
	
		End If
	Next

	For Blood.tBlood = Each tBlood
		If Blood\Escaping	
			Blood\Alpha = Blood\Alpha - 0.05
			xEntityAlpha Blood\Entity, Blood\Alpha

			If Blood\Alpha <= 0
				xFreeEntity Blood\Entity
				Delete Blood
			End If
		ElseIf Blood\LiveTime < GameMillis
			Blood\Escaping = True
			Blood\Alpha = 1.0
		End If
	Next
End Function 

;Генерация геометрии для пятная крови
Function CreateBloodMesh()

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

;Сброс модуля эффектов крови
Function ResetBlood()
	For BloodParticle.tBloodParticle = Each tBloodParticle
		xFreeEntity BloodParticle\Entity
		Delete BloodParticle
	Next

	For Blood.tBlood = Each tBlood
		xFreeEntity Blood\Entity
		Delete Blood
	Next
End Function

