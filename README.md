# UAVLocate

![20250313134007](images/20250313134007.png)

# 2025.3 I

## :pushpin: **位置算法**

**（python获取无人机XYZ坐标并算法计算出偏移再传输给springboot,通过service层修改数据库）**

**生成实体（entity）：数据-附带Service类**​​

<img src="images/202531.png" alt="20250313134007" style="zoom:50%;" />

**x轴坐标posX，y轴坐标posY，z轴坐标posZ。**

**设定出参考点的坐标，固定无人机的飞行速度或通过摄像机确定（复杂），**

**并测量出屏幕坐标移动1个单位现实移动的距离，即可计算XY轴偏离。**

**根据风速风向以及Z轴距离估算降落点最终偏移。**

**举例：参考点设为`（10，10，10）`，假定屏幕1单位对应现实`0.02m`，风速`0.02m/s`，风向为`北偏西30°`。**

**（近地情况无需考虑垂直风向）**

**那么算法实现过程如下：**

**拿到无人机位置坐标`(32，56，207)`，计算出相对坐标`(22，46，197)`，对应现实三维距离`(0.44m,0.92m,3.94m)`**

**无人机下降速度固定为`0.4m/s`。**

**那么下降所需时间为`197\*0.02/0.4=9.85s`，**

**风速为`1000m/h`根据风向可拆分为X轴风速和Y轴风速，**

**<img src="images/202532.png" alt="20250313134007" style="zoom:50%;" />**

**风将使无人机产生的X轴偏移`(-1)0.02·sin(30°)\*9.85=-0.0985m`**

**风将使无人机产生的Y轴偏移`(+1)0.02·cos(30°)\*9.85=0.1706m`**

**那么最终偏移即`(0.44-0.0985,0.92+0.1706)`。**

**首先就这样，也不知道风向能不能通过风速计获取度数。**

### **Python** :link:

**可以直接在Python中实现算法，也可以传入Springboot后。**

```python
import requests
import math

# 无人机当前位置
current_position = (32, 56, 207)  # (X, Y, Z)

# 参考点位置
reference_position = (10, 10, 10)  # (X, Y, Z)

# 风速和风向
wind_speed = 0.02  # m/s
wind_direction = 30  # 北偏西30°，即30°

# 计算相对坐标
relative_position = (
    current_position[0] - reference_position[0],
    current_position[1] - reference_position[1],
    current_position[2] - reference_position[2]
)

# 计算现实中的距离
real_position = (
    relative_position[0] * 0.02,
    relative_position[1] * 0.02,
    relative_position[2] * 0.02
)

# 计算下降时间
descent_speed = 0.4  # m/s
descent_time = real_position[2] / descent_speed

# 计算风向的X轴和Y轴分量
wind_x = wind_speed * math.sin(math.radians(wind_direction))
wind_y = wind_speed * math.cos(math.radians(wind_direction))

# 计算风引起的偏移
wind_offset_x = -wind_x * descent_time
wind_offset_y = wind_y * descent_time

# 计算最终偏移
final_offset = (
    real_position[0] + wind_offset_x,
    real_position[1] + wind_offset_y
)

# 打印结果
print(f"相对坐标: {relative_position}")
print(f"现实距离: {real_position}")
print(f"下降时间: {descent_time} 秒")
print(f"风引起的偏移: X轴 {wind_offset_x} m, Y轴 {wind_offset_y} m")
print(f"最终偏移: {final_offset}")

# 将数据发送到Spring Boot后端
url = "http://localhost:8080/api/drone/position"
data = {
    "x": current_position[0],
    "y": current_position[1],
    "z": current_position[2],
    "final_offset_x": final_offset[0],
    "final_offset_y": final_offset[1]
}
response = requests.post(url, json=data)
print(f"后端响应: {response.text}")
```

### **SpringBoot** :link:

```java
@RestController
@RequestMapping("/api/drone")
public class DronePositionResource {

  @Autowired
  private DronePositionRepository dronePositionRepository;

  @PostMapping("/position")
  public ResponseEntity<String> updateDronePosition(@RequestBody DronePosition position) {
    // 保存数据到数据库
    dronePositionRepository.save(position);
    return ResponseEntity.ok("更新数据成功");
  }
}

```

## **:question: Z坐标获取**

**查阅了一些网页，要获取物体三维坐标的方法有很多，但是双目，三目摄像头获取到的坐标都不够准确。**

**单目+传感器其实是最佳实现方法，不过数据传输速度过慢也是问题，这时我就看到了深度相机，可以使用ai模型**。

**资金充足的情况下想要使用的是Inter的D435i深度相机，那资金不充足怎么办呢**。

# 2025.3 II

## :ballot_box_with_check: 项目重启

**做完公司项目之后回来看发现vite报错。**​​

**<img src="images/202534.png" alt="20250313134007" style="zoom:108%;" />**

**vite是因为nodejs版本被我回退了，用nvm切换到新版本之后把package.json和node_moudles删除后重新安装依赖就没问题了。**

**然后在导入JDL实体的时候jhipster挂了，**

**jhipster为什么会失踪，原来是我重装node的时候把所有的包都清理了。**​​

<img src="images/202533.png" alt="20250313134007" style="zoom:90%;" />

**成功导入JDL实体。**​​

<img src="images/202535.png" alt="20250313134007" style="zoom:95%;" />

**启动Springboot，报错端口被占用。**​​

<img src="images/202536.png" alt="20250313134007" style="zoom: 67%;" />

**找到被占用的端口，终止进程。**

<img src="images/202537.png" alt="20250313134007" style="zoom: 67%;" />

**Springboot启动成功​ :v:**​

<img src="images/202538.png" alt="20250313134007" style="zoom: 67%;" />

**在Springboot配置文件application-dev.yml中配置mySql数据库，关闭liquibase伪数据生成。**:paperclip:

<img src="images/202539.png" alt="20250313134007" style="zoom: 67%;" />

**数据界面完成，后面会把数据设为只读。**

**预计实现的内容：（2选1）**

:o: **方案一：每隔几秒更新一条新的数据，并将数据按倒序排列，新数据会显示在最上方。**

:speech_balloon: **问题：每隔几秒的时间间隔需要固定可能在某些情况下无法加载出完整数据。**

:o: **方案二：每有数据刷新时更新一条新的数据，并将数据按倒序排列，新数据会显示在最上方。**

:speech_balloon: **问题：每有数据刷新就更新可能会时快时慢地刷出数据，不便于测试和查阅。**

**暂且选择第一种方案，先把刷新时间设的久一些，后续再调整。**​​

<img src="images/2025310.png" alt="20250313134007" style="zoom: 67%;" />

**在i18n配置中文：**

```json
{
  "uavLocateApp": {
    "statics": {
      "home": {
        "title": "降落点监测数据",
        "refreshListLabel": "刷新",
        "createLabel": "创建新数据",
        "createOrEditLabel": "创建或编辑数据",
        "notFound": "没有找到数据"
      },
      "created": "数据 { param } 创建成功",
      "updated": "数据 { param } 更新成功",
      "deleted": "数据 { param } 删除成功",
      "delete": {
        "question": "你确定要删除数据 { id } 吗？"
      },
      "detail": {
        "title": "降落点监测数据"
      },
      "id": "序号",
      "time": "时间",
      "posX": "X轴偏移",
      "posY": "Y轴偏移",
      "posZ": "Z轴距离",
      "windSpeed": "风速",
      "windDirection": "风向"
    }
  }
}
```

**效果：**

<img src="images/2025312.png" alt="20250313134007" style="zoom: 67%;" />

### SpringBoot :link:

**用Pageable实现倒序排列数据：**

```java
//Service类
@Transactional(readOnly = true)
public Optional<Statics> findOne(Long id) {
  LOG.debug("Request to get Statics : {}", id);
  return staticsRepository.findById(id);
}

```

**Resource类调用并用getContent()传出排序后的List：**

```java
@GetMapping("")
public List<Statics> getAllStatics(@RequestParam(name = "sort", required = false, defaultValue = "id,desc") String sort) {
  LOG.debug("REST request to get all Statics");
  Pageable pageable = PageRequest.of(0, 20);
  if (sort != null && !sort.isEmpty()) {
    String[] sortParams = sort.split(",");
    Sort.Direction direction = "desc".equalsIgnoreCase(sortParams[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;
    pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortParams[0]));
  }
  return staticsService.findAll(pageable).getContent();
}

```

**效果：**

<img src="images/2025313.png" alt="20250313134007" style="zoom: 67%;" />

# 2025.3 III

## :sun_behind_large_cloud: 气象API

**由于风速计部署及数据传输过于复杂等原因，改用从API获取数据并传输到Spring Boot中。**

:o:**使用API获取数据的优缺点：**

**API提供了标准化的数据接口，可以直接通过HTTP请求获取数据，无需复杂的硬件设备和现场部署。**

**数据通常以结构化的格式（如JSON或XML）返回，易于解析和处理。**

**并且许多气象API提供实时数据或高频率更新的数据，能够满足对实时气象信息的需求。**

**不过API提供商可能会调整数据格式或接口，需要及时更新代码。而风力计可以实时测量当前的风速和风向，数据获取几乎无延迟。**

**然后是最恐怖的一点：主流气象API每个月都要支付四位数以上的价格。:laughing:**

**因此也固定算法实现的位置为Springboot的Service类，而不再是使用python，python仅提供无人机的三维位置数据。**

<img src="images/2025316.png" alt="20250313134007" style="zoom: 60%;" />

:warning:<font color = red>**若能直接传输到springboot中，python也将停用，但目前仍未选择摄像头。**</font>

**这里选用的是心知天气的API，每五分钟可传输一次数据，足以满足我们的需求。**

<img src="images/2025314.png" alt="20250313134007" style="zoom: 100%;" />

**由上图可见接口传输过来的我们所需要的数据为风速`wind_speed`和风向角度`wind_direction_degree`，**

**其中风向角度的数据为整数，那么应该就是0~360的度数，以此对算法度数计算进行更新。**

**具体分析如下：**

**以y轴正半轴为0度，向右增加度数，那么风向度数可以按照以下规则转换为方向：**

**对于介于两个方向之间的度数，可以描述为“偏”某个方向。例如，45°可以描述为“北偏东45度”。**

**计算各个方向的风速:**

**经过测试，心知天气免费版无法获得风速和风向角度数据，不过没有关系，可以试用14天。**

**那么在这之前，先接收一下其它可获取的数据，测试一下API的连通性。**

# 2025.3 IV

## :sun_behind_large_cloud: 气象API-和风天气

**今天查阅的时候发现了另一款有360度风向以及每小时风速的免费API：和风天气，每日1000次的访问量已经足够。**

<img src="images/2025315.png" alt="20250313134007" style="zoom: 90%;" />

**需要的数据：**

- **`now.wind360` [风向](https://dev.qweather.com/docs/resource/wind-info/#wind-direction)，360角度**
- **`now.windSpeed` [风速](https://dev.qweather.com/docs/resource/wind-info/#wind-speed)，公里/小时**

**API格式：`devapi.qweather.com/v7/weather/now?location=&key=`**

**生成的APIKey：`ec15c8e809f54b009f3ba76ab88c542c`**

**需要使用的城市id：**

**上海市**

- **城市ID: `101020100`**

**德阳市**

- **城市ID: `101272001`**

**:white_check_mark:成功获取到德阳市实时天气数据的 JSON**

<img src="images/2025317.png" alt="20250313134007" style="zoom: 90%;" />

## Springboot​ :link:

**weatherData:**

```java
package com.chenhy.domain;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "weather_data")
public class WeatherData implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String locationId;
  private Integer temperature;
  private Integer windSpeed;
  private String windDirection;
  private String observationTime; // 修改为 String 类型
  // 下面是getter和setter
}

```

**config类中定义restTemplate：**

```java
@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    // 自定义 SimpleClientHttpRequestFactory 设置超时
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(5000); // 连接超时 5 秒
    requestFactory.setReadTimeout(30000); // 读取超时 30 秒

    // 使用自定义 RequestFactory 构建 RestTemplate
    RestTemplate restTemplate = builder.requestFactory(() -> requestFactory).build();
    return restTemplate;
  }
}

```

**创建service类：**

```java
package com.chenhy.service;

import com.chenhy.domain.WeatherData;
import com.chenhy.repository.WeatherDataRepository;
import com.chenhy.service.dto.HeWeatherNowResponse;
import com.chenhy.service.mapper.WeatherMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class HeWeatherService {

  private final Logger log = LoggerFactory.getLogger(HeWeatherService.class);

  private final WeatherDataRepository weatherDataRepository;
  private final WeatherMapper weatherMapper;
  private final RestTemplate restTemplate;

  // 配置参数
  @Value("${heweather.api.key}")
  private String apiKey;

  @Value("${heweather.api.url}")
  private String apiUrl;

  public HeWeatherService(WeatherDataRepository weatherDataRepository, WeatherMapper weatherMapper, RestTemplate restTemplate) {
    this.weatherDataRepository = weatherDataRepository;
    this.weatherMapper = weatherMapper;
    this.restTemplate = restTemplate;
  }

  @Transactional
  public HeWeatherNowResponse fetchAndSaveWeather(String locationId) {
    HeWeatherNowResponse response = fetchFromApi(locationId);
    WeatherData weatherData = weatherMapper.toEntity(response);
    weatherData.setLocationId(locationId);
    WeatherData savedData = weatherDataRepository.save(weatherData);
    return weatherMapper.toDto(savedData);
  }

  private HeWeatherNowResponse fetchFromApi(String locationId) {
    String url = String.format("%s?location=%s&key=%s", apiUrl, locationId, apiKey);
    return restTemplate.getForObject(url, HeWeatherNowResponse.class);
  }
}

```

**创建repository类：**

```java
package com.chenhy.repository;

import com.chenhy.domain.WeatherData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
  // 添加自定义查询方法
  WeatherData findFirstByLocationIdOrderByObservationTimeDesc(String locationId);
}

```

**根据返回的 JSON格式创建DTO：**

```java
package com.chenhy.service.dto;

import java.util.List;

public class HeWeatherNowResponse {

  private String code;
  private String updateTime;
  private String fxLink;
  private Now now;
  private Refer refer;

  // Getters和Setters

  public static class Now {

    private String obsTime;
    private Integer temp;
    private String feelsLike;
    private String icon;
    private String text;
    private String wind360;
    private String windDir;
    private String windScale;
    private Integer windSpeed;
    private String humidity;
    private String precip;
    private String pressure;
    private String vis;
    private String cloud;
    private String dew;
    // Getters和Setters
  }

  public static class Refer {

    private List<String> sources;
    private List<String> license;
    // Getters和Setters
  }
}

```

**路由控制器：**

```java
package com.chenhy.web.rest;

import com.chenhy.domain.WeatherData;
import com.chenhy.service.HeWeatherService;
import com.chenhy.service.dto.HeWeatherNowResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherResource {

  private final Logger log = LoggerFactory.getLogger(WeatherResource.class);

  private final HeWeatherService heWeatherService;

  public WeatherResource(HeWeatherService heWeatherService) {
    this.heWeatherService = heWeatherService;
  }

  @GetMapping("/{locationId}")
  public ResponseEntity<HeWeatherNowResponse> getWeatherData(@PathVariable String locationId) {
    log.debug("REST request to get weather for location: {}", locationId);
    HeWeatherNowResponse result = heWeatherService.fetchAndSaveWeather(locationId);
    return ResponseEntity.ok(result);
  }
}

```

# 2025.3 V

**测试昨天写的API接口，一直报错返回的 JSON含有非法字符，对非法字符处理后仍然无法正常接收到 JSON。**

**今天回去和风天气的开放文档又看了看，发现其API返回的数据均使用了Gzip压缩，那么接下来对Gzip进行处理。**

## Springboot :link:

```java
@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    // 自定义 SimpleClientHttpRequestFactory 设置超时
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(5000); // 连接超时 5 秒
    requestFactory.setReadTimeout(30000); // 读取超时 30 秒

    // 使用自定义 RequestFactory 构建 RestTemplate
    RestTemplate restTemplate = builder.requestFactory(() -> requestFactory).build();

    // 添加 Gzip 解压缩拦截器
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
    interceptors.add(new GzipDecompressingClientHttpRequestInterceptor());
    restTemplate.setInterceptors(interceptors);

    return restTemplate;
  }

  // 自定义 Gzip 解压缩拦截器
  public static class GzipDecompressingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
      ClientHttpResponse response = execution.execute(request, body);
      if (isGzipResponse(response)) {
        return new GzipDecompressingClientHttpResponse(response);
      }
      return response;
    }

    private boolean isGzipResponse(ClientHttpResponse response) {
      return "gzip".equalsIgnoreCase(response.getHeaders().getFirst("Content-Encoding"));
    }

    private static class GzipDecompressingClientHttpResponse implements ClientHttpResponse {

      private final ClientHttpResponse response;

      public GzipDecompressingClientHttpResponse(ClientHttpResponse response) {
        this.response = response;
      }

      @Override
      public InputStream getBody() throws IOException {
        return new GZIPInputStream(response.getBody());
      }

      @Override
      public HttpStatusCode getStatusCode() throws IOException {
        return response.getStatusCode();
      }

      @Override
      public String getStatusText() throws IOException {
        return response.getStatusText();
      }

      @Override
      public void close() {
        response.close();
      }

      @Override
      public org.springframework.http.HttpHeaders getHeaders() {
        return response.getHeaders();
      }
    }
  }
}

```

**在mySql中创建用来接受天气数据的表：**

**<img src="images/2025318.png" alt="20250313134007" style="zoom: 90%;" />**

**成功获得气象数据并存入数据库表：**

**<img src="images/2025319.png" alt="20250313134007" style="zoom: 90%;" />**

**调用测试：**

**<img src="演示视频/v202531.gif" alt="20250313134007" style="zoom: 260%;" />**

**测试页面:/weather：**

<img src="演示视频/v202532.gif" alt="20250313134007" style="zoom: 260%;" />

**至此已完成气象API的调用，获得的数据是wind360和windSpeed，并存在表weather_data中。**
