# Little Intermediary Fallback
This is an obfuscation compatibility layer that implements a fallback for obfuscation (intermediary), as obfuscation has been removed since version 26.1.
26.1以降で難読化が解除されたため、難読化(中間名)のフォールバックを実装したobf互換レイヤーです。

It is only for mods that are based on MCPitanLib, but you can make it available for external mods with Config.
MCPitanLibを前提としているmodのみ対象ですが、Configで外部modも対象にすることができます。

## Config (config/littleintermediaryfallback.json)
```json
{
  "modsHash": "42b60aa2",
  "targetPackages": [
    "net/pitan76/enhancedquarries/",
    "net/pitan76/ordinarycrook/",
    "net/pitan76/ygm76/",
    "net/pitan76/itemalchemy/",
    "net/pitan76/simplecables76/"
  ],
  "enabled": true
}
```

### English
- `modsHash`: Hash value of the mod list. It changes when mods are added, removed, or updated, and is used to determine whether targetPackages needs to be updated again.
- `targetPackages`: A list of packages that are subject to name conversion from obfuscated names to deobfuscated names. Mods that are based on MCPitanLib are automatically added.
- `enabled`: A flag that determines whether to convert obfuscated names to deobfuscated names. If set to false, Little Obf Fallback will not function.

### 日本語
- `modsHash`: Modリストのハッシュ値。Modの追加や削除、更新などで変わり、targetPackagesを再度更新する必要があるかどうかの判断に使用されます。
- `targetPackages`: 中間名の変換対象となるパッケージのリスト。mcpitanlibを前提とするmodは自動的に追加されます。
- `enabled`: 中間名を難読化解除後の名前に変換するかどうかのフラグ。falseにすると、Little Intermediary Fallbackは機能しなくなります。