# ui-react

基于react实现的前端页面

## 安装

版本: 

- nodejs v18.19.0 

```bash
pnpm install
```

本地开发

```bash
pnpm dev
```

打包

```bash
pnpm build
```

打包并发布静态页面到SpringBoot项目的静态目录下

通过自定义脚本来定义发布命令

```json
"scripts": {
  "build:static": "next build",
  "clean:to-spring": "rimraf ../app/src/main/resources/static/*",
  "copy:to-spring": "cpx \"out/**/*\" ../app/src/main/resources/static/",
  "deploy": "npm run build:static && npm run clean:to-spring && npm run copy:to-spring"
}
```

一键发布方式

```bash
pnpm run deploy
```