#!/bin/bash
#Gotta make $u bc otherwise it will assume the underscore name is a part of the var name?
#Idk i suck at bash i'm just making this cause i'm lazy and like scripts
#And java doesnt have macros cause it sux
u="_"
tool()
{
echo "case $1$u$2:	return new CraftedUtility(Item.TOOL_$2, ItemMaterial.$1);"
}
armor()
{
echo "case $1$u$2:	return new CraftedUtility(Item.ARMOR_$2, ItemMaterial.$1);"
}

toolmaterial()
{
tool $1 "SWORD"
tool $1 "SPADE"
tool $1 "PICKAXE"
tool $1 "AXE"
tool $1 "HOE"
}
armormaterial()
{
armor $1 "HELMET"
armor $1 "CHESTPLATE"
armor $1 "LEGGINGS"
armor $1 "BOOTS"
}

toolmaterial "WOOD"
toolmaterial "STONE"
toolmaterial "IRON"
toolmaterial "GOLD"
toolmaterial "DIAMOND"

armormaterial "LEATHER"
armormaterial "CHAINMAIL"
armormaterial "IRON"
armormaterial "GOLD"
armormaterial "DIAMOND"
