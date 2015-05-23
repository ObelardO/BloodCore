;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль взрывов
;==================================================================

;Константы для текстуры взрыва
Const ExplosionTextureFrameCount    = 25
Const ExplosionTextureFrameSize     = 200
Const ExplosionTextureFrameLiveTime = 20
Const ExplosionRadius = 13

;Текстура и звук
Global ExplosionTexture
Global ExplosionSound

;Структура взрыва
Type tExplosion
	Field Entity
	Field TextureID
	Field TextueFrame
	Field TextureTime
End Type

;Инициализация модуля взрывов
Function InitExplosin()
	ExplosionTexture = xLoadAnimTexture("base\textures\explosion.jpg", FLAGS_ALPHA, ExplosionTextureFrameSize,ExplosionTextureFrameSize, 0, ExplosionTextureFrameCount)
	ExplosionSound   = xLoad3DSound("base\sounds\explosion\Explosion.wav")
End Function

;Добавление Взрыва
Function CreateExplosion(ExplosinX#, ExplosionY#, ExplosionZ#)
	Explosion.tExplosion = New tExplosion

	Explosion\Entity = xCreateSprite()
	Explosion\TextureTime = GameMillis + ExplosionTextureFrameLiveTime
	xEntityTexture  Explosion\Entity, ExplosionTexture, 0
	xEntityFX       Explosion\Entity, FX_FULLBRIGHT
	xPositionEntity Explosion\Entity, ExplosinX, ExplosionY, ExplosionZ
	xScaleSprite    Explosion\Entity, 5, 5
	xRotateSprite   Explosion\Entity, Rand(0, 360)

	xEmitSound ExplosionSound,  Explosion\Entity

	ExplosionToPlayerDistance# = xEntityDistance(Explosion\Entity, PlayerCollider) 
	If ExplosionToPlayerDistance < ExplosionRadius
		PlayerHealth = PlayerHealth - (ExplosionRadius - ExplosionToPlayerDistance) * 10
		CreateBloodPatricle(PlayerX, PlayerHeight, PlayerZ)
		CreateBloodPatricle(PlayerX, PlayerHeight, PlayerZ)
		CreateBlood(PlayerX, PlayerZ)
		PlayerEffectBlood = 5.0
	End If
End Function

;Оновление взрывов
Function UpdateExplosions()
	For Explosion.tExplosion = Each tExplosion
		xMoveEntity Explosion\Entity, 0, 0.5, 0
		xScaleSprite  Explosion\Entity, 5 +  Explosion\TextueFrame, 5 +  Explosion\TextueFrame
		If Explosion\TextureTime < GameMillis
			Explosion\TextueFrame = Explosion\TextueFrame + 1
			xEntityTexture  Explosion\Entity, ExplosionTexture, Explosion\TextueFrame

			Explosion\TextureTime = GameMillis + ExplosionTextureFrameLiveTime

			If Explosion\TextueFrame = ExplosionTextureFrameCount
				xFreeEntity Explosion\Entity
				Delete Explosion
			End If
		End If
	Next
End Function
