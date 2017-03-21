# CapesAPI Client Implemention

## About

This is a fork of the CapesAPI Client Implementation by halfpetal. Differences between this version and the original are as follows:

- This version includes installation instructions for non-OptiFine clients.

- This version uses text-based installation instructions instead of screenshots, allowing users to quickly copy-paste the required modifications into their clients.

- This version includes pre-compiled releases, allowing users to use CapesAPI as a library instead of adding the `com.capesapi` package to their client's source code.

- The source code of this version has been formatted according to the Wurst-Imperium code style.

## Compatibility

This CapesAPI implementation is compatible to Minecraft 1.11.2, 1.11, 1.10.2, 1.10, 1.9.4 and 1.9.

It is also possible to run this implementation on Minecraft 1.8 with some additional modifications.

This implementation does not require OptiFine, but it is compatible with it.

## Installation

This CapesAPI implementation can either be installed by adding the `com.capesapi` package to your client's source code or by adding the pre-compiled version to your client's libraries.

After that, CapesAPI requires two small modifications in `net.minecraft.client.entity.AbstractClientPlayer` in order to work. What these modifications look like depends on whether or not OptiFine is used.

### Standard
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

### With OptiFine
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
