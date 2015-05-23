;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль предметов
;==================================================================

;Параметры текстуры предмета
Const ItemImageFrameCount = 8
Const ItemImageFrameSize  = 64

;Типы предметов
Const ItemHealthSmall = 0
Const ItemHealthLarge = 1
Const ItemArmoSmall   = 2
Const ItemArmoLarge   = 3
Const ItemAmmoShotGun = 4
Const ItemAmmoMiniGun = 5
Const ItemAmmoRocket  = 6
Const ItemAmmoPacket  = 7
Const ItemAmmoNewGun  = 8

;Режим предмета
Const ItemStatusBorn = 0
Const ItemStatusWait = 1

;Текстура предметов
Global ItemTexture

;Флаги экстренных предметов
Global ExtrimalAmmoSpawned
Global ExtrimalHealthSpawned

;звук предмета
Dim ItemSound(ItemImageFrameCount)

;структура предмета
Type tItem
	Field Entity
	Field X, Z
	Field Status
	Field Displace#
	Field ItemType
End Type

;Инициализация предметов
Function InitItems()
	ItemTexture = xLoadAnimTexture("base\textures\Items.png", FLAGS_ALPHA, ItemImageFrameSize, ItemImageFrameSize, 0,ItemImageFrameCount + 1)

	For i = 0 To ItemImageFrameCount
		ItemSound(i) = xLoad3DSound("base\sounds\item\item" + (i + 1) + ".wav")
	Next
End Function

;Добавление предмета
Function CreateItem(ItemType, ItemCount = 1, Extremal = False )
	For i = 1 To ItemCount
		Item.tItem = New tItem
	
		Item\ItemType = ItemType
		Item\X = Rand(-8, 8) * 10 + Rand(-5, 5)
		Item\Z = Rand(-8, 8) * 10 + Rand(-5, 5)
	
		Item\Entity = xCreateSprite()
		xPositionEntity Item\Entity, Item\X, 2.0, Item\Z
		xEntityTexture  Item\Entity, ItemTexture, ItemType
		xEntityAlpha    Item\Entity, 0.0
		xEntityFX       Item\Entity, FX_FULLBRIGHT

		If ItemType = ItemAmmoShotGun And Extremal = True And GetItemCount(ItemAmmoShotGun) = 0 ExtrimalAmmoSpawned   = True
		If ItemType = ItemHealthSmall And Extremal = True And GetItemCount(ItemHealthSmall) = 0 ExtrimalHealthSpawned = True 
	Next
End Function

;Обновление предметов
Function UpdateItems()
	;Перебор предметов
	For Item.tItem = Each tItem
		;Перебор режима предмета
		Select Item\Status
			;Появление
			Case ItemStatusBorn
				If Item\Displace < 1.0 Item\Displace = Item\Displace + 0.1

				xEntityAlpha Item\Entity, Item\Displace

				If Item\Displace >= 1.0
					Item\Status = ItemStatusWait
				End If
			;Ожидание
			Case ItemStatusWait
				xPositionEntity Item\Entity, Item\X, Sin(GameMillis * 0.8) * 0.04 + 2.0,Item\Z

				If Abs(PlayerX - Item\X) < 3 And Abs(PlayerZ - Item\Z) < 3
				
					Select Item\ItemType
						Case ItemHealthSmall
							PlayerHealth = PlayerHealth + 100
							If PlayerHealth > 1000 PlayerHealth = 1000
							
						Case ItemHealthLarge
							PlayerHealth = 1000
							
						Case ItemArmoSmall
							PlayerArmo = PlayerArmo + 100
							If PlayerArmo > 1000 PlayerArmo = 1000
							
						Case ItemArmoLarge
							PlayerArmo = 1000
							
						Case ItemAmmoShotGun
							GunShotGunAmmo = GunShotGunAmmo + 40
							
						Case ItemAmmoMinigun
							GunMinigunAmmo = GunMinigunAmmo + 300
							
						Case ItemAmmoRocket
							GunRocketAmmo = GunRocketAmmo + 6
							
						Case ItemAmmoNewGun
							GunNotAdded = True
							If GunNotAdded And GunShotGunAdded < 2 CreateGun(GunNameShotGun) GunNotAdded = False
							If GunNotAdded And GunMiniGunAdded < 2 CreateGun(GunNameMiniGun) GunNotAdded = False
							If GunNotAdded And GunRocketAdded  < 2 CreateGun(GunNameRocket)  GunNotAdded = False
						
					End Select
						
					xEmitSound (ItemSound(Item\ItemType), PlayerCollider)
					xFreeEntity Item\Entity

					PlayerEffectItem = 1.0

					Delete Item
				End If
		End Select
	Next
End Function

;Сброс всех предметов
Function ResetItems()
	For Item.tItem = Each tItem
		xFreeEntity Item\Entity
		Delete Item
	Next
	ExtrimalAmmoSpawned = False
	ExtrimalHealthSpawned = False
End Function

;Получить кол-во предметов определенного типа
Function GetItemCount(ItemType)
	For Item.tItem = Each tItem
		If Item\ItemType = ItemType Result = Result + 1
	Next

	Return Result
End Function

;Добавить предметы во время нового этапа
Function SpawnItemsBreak()
	CreateItem(ItemHealthSmall, GameVaweStep)
	CreateItem(ItemArmoSmall, GameVaweStep)
	
	If GunShotGunAdded CreateItem(ItemAmmoShotGun, GunShotGunAdded * GameVaweStep)
	If GunMinigunAdded CreateItem(ItemAmmoMiniGun, GunMinigunAdded * GameVaweStep)
	If GunRocketAdded  CreateItem(ItemAmmoRocket,  GunRocketAdded  * GameVaweStep) 
End Function

;Добавить предметы во время новой волны
Function SpawnItemsVawe()
	CreateItem(ItemHealthLarge)
	CreateItem(ItemArmoLarge)

	If GunShotGunAdded CreateItem(ItemAmmoShotGun, GunShotGunAdded * GameVaweStep)
	If GunMinigunAdded CreateItem(ItemAmmoMiniGun, GunMinigunAdded * GameVaweStep)
	If GunRocketAdded  CreateItem(ItemAmmoRocket,  GunRocketAdded  * GameVaweStep)

	If GunRocketAdded + GunMiniGunAdded + GunShotGunAdded CreateItem(ItemAmmoNewGun)
End Function
