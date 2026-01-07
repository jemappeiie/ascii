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
java -jar ascii-<version>.jar --input <path-to-image> [options]
```

### Arguments and options

| Parameter | Type | Required | Description                                            |
|:----------|:--|:---------|:----------------------------------------------------------|
| --input   | String | Yes      | Path to the source file.                             |
| --output  | String | No       | Path to the output file.                             |
| --width   | Int | No       | Target width in characters. (Default: 100)              |
| --height  | Int | No       | Target height in characters.                            |
| --invert  | Flag | No       | Inverts the brightness levels of the generated art.    |

**Note:** If `--height` is omitted, the tool automatically calculates it based on the source image’s dimensions. This calculation is specially adjusted to account for the rectangular shape of terminal characters, ensuring your ASCII art doesn't look "squashed" or "stretched"  
**Note:** If `--output` is omitted, the art is printed directly to the console.
### Example 
```console
java -jar ascii-1.0.0.jar --input ./images/cat.jpg --output ./cat.txt --width 80 --invert
```
## 🗺️ Roadmap

- Save to file ✅
- Alpha blending ✅
- Parallel decomposition
- ASCII to image
