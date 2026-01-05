# ASCII

This is a command-line interface utility for converting images to ASCII art.

## 🚀 Getting Started

### Prerequisites
Before running the tool, ensure you have JDK 24 or higher installed on your machine. You can verify your version by running:

```console
java -version
```

### Installation
Go to the latest release and download the `.jar` file. That's it.
## 🛠️ Usage

Open your terminal, navigate to the directory where you saved the file, and execute the following command.

```console
java -jar ascii-<version>-all.jar --path <path-to-image> [options]
```

### Arguments and options

| Parameter | Type | Required | Description |
|:--|:--|:--|:--|
| --path | String | Yes | The local file path to the source image. |
| --width | Int | No | Target width in characters. (Default: 100) |
| --height | Int | No | Target height in characters. |
| --invert | Flag | No | Inverts the brightness levels of the generated art. |

**Note:** If `--height` omitted, the tool automatically calculates it based on the source image’s dimensions. This calculation is specially adjusted to account for the rectangular shape of terminal characters, ensuring your ASCII art doesn't look "squashed" or "stretched."

### Example 
```console
java -jar ascii-1.0.0-all.jar --path ./images/cat.jpg --width 80 --invert
```
## 🗺️ Roadmap

- Save to file
- Alpha blending ✅
- Parallel decomposition
- ASCII to image
