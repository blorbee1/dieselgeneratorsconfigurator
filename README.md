Want to rebalance the fuels in Create Diesel Generators? Diesel Generators Configurator adds a configuration file to allow just that.

## Configuration

On first launch, the mod generators a configuration file at:

`config/dieselgeneratorsconfig/fuel_overrides.json`

The file contains an entry for every registered fuel type, example:

```json
{
  "createdieselgenerators:plantoil": {
    "normal": {
      "speed": 74.0,
      "strength": 2048.0,
      "burn_rate": 0.48
    },
    "modular": {
      "speed": 74.0,
      "strength": 2560.0,
      "burn_rate": 0.72
    },
    "huge": {
      "speed": 192.0,
      "strength": 4096.0,
      "burn_rate": 1.44
    },
    "burner_multiplier": 0.5
  }
}
```

| Property | Description |
|---|---|
| `speed` | Rotational speed provided by the fuel. |
| `strength` | Stress capacity provided by the fuel. |
| `burn_rate` | Amount of fuel consumed per tick. |
| `burner_multiplier` | Multiplier applied to the fuel burner's strength. |

## Requirements

- [Create Diesel Generators](https://modrinth.com/mod/create-diesel-generators)