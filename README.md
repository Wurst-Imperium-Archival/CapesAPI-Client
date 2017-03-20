### NOTE TO NON-OPTIFINE DEVELOPERS
#### Please be aware that this implementation was built with Optifine, so there's a chance if you use this that you may have to alter it slighty.

### NOTE TO 1.8 DEVELOPERS
#### Some of the methods in 1.8 MCP are still obfuscated, so you may have issues with wrong method names. Just rename the methods in the implementation, or you can manually write it. 
#### Back up your files **FIRST** before you do anything.

# CapesAPI Implemention
## About
This client implementation is built for client developers who want an easy way to throw [CapesAPI](http://capesapi.com) integration into their clients.

It's suggested that you also have info somewhere (like a `.capes` command) on where they can change their capes and such.

## Versions
| Implementation Version | Minecraft Version(s) |
|------------------------|----------------------|
| 2.2.1                  | 1.11.2/1.10.2        |
| 2.2.0                  | 1.11.2/1.10.2        |
| 2.1.1                  | 1.11.2/1.10.2        |
| 2.1.0                  | 1.11.2/1.10.2        |
| 2.0.0                  | 1.11.2/1.10.2        |

# Installation

CapesAPI requires two small modifications in `net.minecraft.client.entity.AbstractClientPlayer` in order to work. What these modifications looks like depends on whether or not OptiFine is used.

## Standard
```java
public AbstractClientPlayer(World worldIn, GameProfile playerProfile)
{
    super(worldIn, playerProfile);

    // CapesAPI
    CapesAPI.loadCape(playerProfile.getId());
}
```

```java
@Nullable
public ResourceLocation getLocationCape()
{
    // CapesAPI
    if(CapesAPI.hasCape(getGameProfile().getId()))
        return CapesAPI.getCape(getGameProfile().getId());

    NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
    return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
}
```

## With OptiFine
```java
public AbstractClientPlayer(World worldIn, GameProfile playerProfile)
{
    super(worldIn, playerProfile);
    this.nameClear = playerProfile.getName();

    if(this.nameClear != null && !this.nameClear.isEmpty())
    {
        this.nameClear = StringUtils.stripControlCodes(this.nameClear);
    }

    CapeUtils.downloadCape(this);
    PlayerConfigurations.getPlayerConfiguration(this);

    // CapesAPI
    CapesAPI.loadCape(playerProfile.getId());
}
```

```java
@Nullable
public ResourceLocation getLocationCape()
{
    if(!Config.isShowCapes())
    {
        return null;
    }else if(this.locationOfCape != null)
    {
        return this.locationOfCape;

        // CapesAPI
    }else if(CapesAPI.hasCape(getGameProfile().getId()))
        return CapesAPI.getCape(getGameProfile().getId());

    else
    {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? null
            : networkplayerinfo.getLocationCape();
    }
}
```
