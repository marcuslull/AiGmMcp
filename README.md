# AI GM MCP Server

This Spring Boot MCP server provides a suite of AI-powered tools designed to assist Game Masters (GMs) in tabletop role-playing games. It includes functionalities such as dice rolling, random encounter generation, and treasure generation, leveraging AI to enhance the GM experience.
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