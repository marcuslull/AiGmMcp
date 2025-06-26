# AI GM MCP Server
___

### Create JAR
```bash

mvn clean package
```

### MCP Inspector mode
```bash
   npx @modelcontextprotocol/inspector java -jar <path to jar file> --spring.profiles.active=pack
```

### JAR mode
```bash
   java -jar <path to jar file> --spring.profiles.active=pack
```

### License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.