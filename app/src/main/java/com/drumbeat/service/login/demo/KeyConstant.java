package com.drumbeat.service.login.demo;

import com.blankj.utilcode.util.StringUtils;

/**
 * @author ZuoHailong
 * @date 2020/4/23
 */
public class KeyConstant {
//    public static String privateKey = "MIIEpAIBAAKCAQEA0b7ZVGs6x7xCxSuUwiACGsNrxsuaadDqVT0tuHRC1TdePDr0DOnjXBGsOmUGu27Vo+lQVe0iWCHkpH7mRn3l3aTMTZv5Vs6RJZEkmw4hpyQA5bQI83PqX0Dk20UyxEirJ9z/NFvmldKXYvnmdq3r0DFIpiegzVj7l8Hvy7Kva+BlgUitxcqFJnUGktnjmWT+59FdWrlD8VbJeNr88jrc9qUP8VJz18qGemOfaanCCOuH0zmiO+DwpvRBBnJ3DT5mmaH6+KudFyvRQ1wxjS8i0h3O18WfD4GGB2hMcBi0t04Sg/QWGxLmtlrKfEgnSh6an3m8ZYydhqyQwXa6pqyLjQIDAQABAoIBAQCMJwrx7fUJIjM7rTNpxBmj6JFL9SDpNEBhVNAIQSencaf2dy48sszJ6MU8/+TPue/n5y9XO22Hlp5BjMoysE25HTWnZmHmL4GtqJ7dPUQfe19eHj8c7ofdvm8uVb7miehUasfdJMufsWiXKoVlKDI6m7P/0tsQXDVB4sVpR60QV1ut3a1CMlYwQgpbuYiHlNzCUs+uI0esQzNqw6vfB+3jWwagZZn32aiNsJFOGbR97wApgV9K0obTvWLCrebbk+UJ4xsSCLCixNuMvb565A4KdLhACu0Xcyaf6j8NFX51AniMrIN9/irQEoethH0gjdLZDdyb6jNDmLyhCCscNrwBAoGBAO561UuhG9ekB8ZYAIQIparRqRyRzQ0NVWlOsO3FZt8SpzkjfxZXlUIPmEFqJoibAMVVwhpFYxNW95SXfaoZs9SYBdM/EIZPCxKgLtMvxJAYWbh58MobCh33F0ugR2+QMBeT/iv7UdzYEFoNojoE4V/BdG6lNlwJP4T4pJ5xdJK9AoGBAOEnmaq5xsKh/ipDPo0cCIobTdd2CP0XAsfPrSdiZnuIdtAAusyKGfQHgKPgBWg+qZQMSG67LloMhmjm9I7KOvhYLIqPNp5uZcgrHrY8SnBoYEWSqLN0ytloa2Kys71f+tN4rSUU/PEXpF9orzfJblCeyciYu46S9kH5/bgaFVERAoGAf51fkm8ONSDjwDHLZNwzuusi8dTbw4ZFFGs1pqch23Fh5uOl2FDZ4FX0Bd1F7Yf0q6Suj9xdnjXFmtBOQL4sSzFmvJpcv2KA7GCnbf970hgVuvAV+DDsAwj8gE2urHaeJZ/ePa7pKaK9bVMqYp3F9LXTFnp2Ul3ojcF4ZgkUdokCgYBeu0CmWl/MSEgeFAjfaioVoYNQ4jLJ+cKLu09JQNeJplMHFSeDI5/j0rxHNtxPjf3fcoOPQ2YvMRHkVTgUU/YXw1GKQtNiOWTNoYbceeWAAgpve1NLnhoy+wIjlriVNyrbiuyeq4P5FE9ubSc7u56UgRq5MbnINQEzpTEhIqhRYQKBgQCjNl+1W7zeIB60LMFyGB58HCNaWsnBeHjDDSbkgRD7Je+QfTaYh9sak1KBuD7IUfL+oyZifCCY9lLLXeWKqRlWunIrSI5WZTcqSyVEfqd4j+3gLkJCFfJ77iWalsKhgBMuJRc7EtNevL8Ic9NZnIheskY1eylpJW9E3cIFFi9HzA==";

    public static String privateKey  = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDRvtlUazrHvELFK5TCIAIaw2vGy5pp0OpVPS24dELVN148OvQM6eNcEaw6ZQa7btWj6VBV7SJYIeSkfuZGfeXdpMxNm/lWzpElkSSbDiGnJADltAjzc+pfQOTbRTLESKsn3P80W+aV0pdi+eZ2revQMUimJ6DNWPuXwe/Lsq9r4GWBSK3FyoUmdQaS2eOZZP7n0V1auUPxVsl42vzyOtz2pQ/xUnPXyoZ6Y59pqcII64fTOaI74PCm9EEGcncNPmaZofr4q50XK9FDXDGNLyLSHc7XxZ8PgYYHaExwGLS3ThKD9BYbEua2Wsp8SCdKHpqfebxljJ2GrJDBdrqmrIuNAgMBAAECggEBAIwnCvHt9QkiMzutM2nEGaPokUv1IOk0QGFU0AhBJ6dxp/Z3LjyyzMnoxTz/5M+57+fnL1c7bYeWnkGMyjKwTbkdNadmYeYvga2ont09RB97X14ePxzuh92+by5VvuaJ6FRqx90ky5+xaJcqhWUoMjqbs//S2xBcNUHixWlHrRBXW63drUIyVjBCClu5iIeU3MJSz64jR6xDM2rDq98H7eNbBqBlmffZqI2wkU4ZtH3vACmBX0rShtO9YsKt5tuT5QnjGxIIsKLE24y9vnrkDgp0uEAK7RdzJp/qPw0VfnUCeIysg33+KtASh62EfSCN0tkN3JvqM0OYvKEIKxw2vAECgYEA7nrVS6Eb16QHxlgAhAilqtGpHJHNDQ1VaU6w7cVm3xKnOSN/FleVQg+YQWomiJsAxVXCGkVjE1b3lJd9qhmz1JgF0z8Qhk8LEqAu0y/EkBhZuHnwyhsKHfcXS6BHb5AwF5P+K/tR3NgQWg2iOgThX8F0bqU2XAk/hPiknnF0kr0CgYEA4SeZqrnGwqH+KkM+jRwIihtN13YI/RcCx8+tJ2Jme4h20AC6zIoZ9AeAo+AFaD6plAxIbrsuWgyGaOb0jso6+Fgsio82nm5lyCsetjxKcGhgRZKos3TK2WhrYrKzvV/603itJRT88RekX2ivN8luUJ7JyJi7jpL2Qfn9uBoVURECgYB/nV+Sbw41IOPAMctk3DO66yLx1NvDhkUUazWmpyHbcWHm46XYUNngVfQF3UXth/SrpK6P3F2eNcWa0E5AvixLMWa8mly/YoDsYKdt/3vSGBW68BX4MOwDCPyATa6sdp4ln949rukpor1tUypincX0tdMWenZSXeiNwXhmCRR2iQKBgF67QKZaX8xISB4UCN9qKhWhg1DiMsn5wou7T0lA14mmUwcVJ4Mjn+PSvEc23E+N/d9yg49DZi8xEeRVOBRT9hfDUYpC02I5ZM2hhtx55YACCm97U0ueGjL7AiOWuJU3KtuK7J6rg/kUT25tJzu7npSBGrkxucg1ATOlMSEiqFFhAoGBAKM2X7VbvN4gHrQswXIYHnwcI1paycF4eMMNJuSBEPsl75B9NpiH2xqTUoG4PshR8v6jJmJ8IJj2Ustd5YqpGVa6citIjlZlNypLJUR+p3iP7eAuQkIV8nvuJZqWwqGAEy4lFzsS0168vwhz01mciF6yRjV7KWklb0TdwgUWL0fM";

//    public static String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCgcMlVyD0dfx9ArRFGiXdvRdSlnxabB8q0XV+yJnSVNaZJde0thWaQE1YQbbZBOBIlBjI3BsyyERND0L9gff87df17gLH4b5wPpRpctmPcWgyqJSIhsD3ZfEzDH+78uJFkxgp849dZt3bMvRmJos+TR8XU+VCPrJBkHzXkM95ckLXaU5QW0l3kAPFn/J/zyuxrmZWb8CxWRMrImah0peiErBFoT7VKVZCBjVx3n0XlhuWI0Rf3enMKDW5UP9M6oIVVR1ZAeWPnWcfk0HER+WBr+l3qHPc+M1bVDLhwC8/4Ms24XzXqRNGn9Vl3Tvh7yuDvw3T95kbS1KmTRDiX+sBfAgMBAAECggEAdIRUg3rvf3txy/ukXzC4T1RMcZeOyDopaUGinPO2mSIkSJdfDz8hVCLmzvmOceDxtHfCghnrEKq77fUs9zup35n54/Z7yWvsz6Pc+FXxQfcX2X0sQ4EF+et+p/qKCnlUBfaZaUfifqJxoZD3efCGhXSLZPdHHYB6QGw2LCvLIjdBUtDwH7yjDkGVtL4+UZ0WlKKTOBvz+2Zz0K1fTbFcU3Cpe9Z4+Uds0V9ZMyYaz02SWI8eckTWadltM7Z26WE198Cgj/lFcI33TZFlxnr6MeA1xS9LJ6to69Pu1Yss6xOxB6M2SMa/9LJfhD4WLKbZTxLHwioSKtHbR40uHt6pAQKBgQDl8kMRqqVQtNS8RL+5GM4YD1Leoo9WLUTcWLn8Hn/VqK/QFKFtoLyS4twnzeBsJfc9HDB+g7stuxR4+1AcV31HslFVoJv/sv4RuQso3aj5xDOV3FkHmGZRoHkI42o9THuWch66WGcrsom7BZhwD5IpvJePbuy5sYEco4+7yvQ6gQKBgQCynndS7iSoDIDfx/H51nIfRXV5A1VWHM8pUo8Huzdzw2pboEjhooyioRW4V5V/zZ57ySDE0ZJrWTQj31+yJ6KMOEVaWreIptIODQsr1nDPPAtD4sXgQmTN9ZUIQKgfKVZzxb9nmULo0E+UGl1jojwHdMUSnGVHoeNovaMw+hLK3wKBgHXvrECKYmGjbZzmMeMwervSWDGB9LxBWZkeFL3fsrcV3p59hlIg57b6jeoIaLtvOYBpTeIFlGIfGp/2XZ87rDCyn46oYnpiBnd2jee20GzSGZcaPbtsicYB5TIO2bV43cZK38G5h6jc2FFIzGdlRRUM+jsBrH6NMOhMZ3Ls5BiBAoGBAJRTT1dIdpwuNd6e1OvZDk+1iEWDZRvrNIRmEGwlJXk2L14DDkKAjzSFe9nmsTi98PtuG44VUfdezhNz7K2uiCbUm5Lafko17XzJjBtNKcSNYfVvnU4LFFaLtsRxnKqz0xy0e6t188ANK+gatK6GDD9M0lIQGXKQlp1lzNxg7bYVAoGAbep4HlsTsblqQ424hhYbKryMJjPjjogDvv7ktySo3ZaP1RnixWFsFMQ8ovRdF522AswEIP8UWVPID6OC0PEcvv0p3SjaqKW+lLj/3Qz5wWYByHVybQdXTn8QMx+lSgUWYAjcn6QTLTqSSrh68FTFjoHcaWYOKTh5tbZ/OGTcuyM=";

//    public static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJOt7XH8C2BaSUg/tl1Yef7VsO7HjeN8WN+I1pHCQO4rZtgYjY5nrfPVKfsF8jJMr6wc3454A56w36UVw9nRJNUX3/94MOIeSlkvQh0CJvOvsVBgEX/ygRBE2Hd/1W7irtQ+GcHPLrCoFXAdr5oD1315/vRJzmnqnWIhioG4Sh2fAgMBAAECgYBeTcDE+whgvQby5gyUSz2MJ/VWZYQL8onmJMGwTnWcIYcJE5TBjR+eB09JhyCEUkgUBlQT7DuTiKsoBmAMpet4UU+GkO4TjJri0z3J+/losvhtgchBzka9xcsGBnSrbg83bjE6BH+yFG8WYN2D8Sv6YScnx6rmlI4Uvl56kujhYQJBAPTsTkJ75ylsf+OguwMNmXavHavle/FirYGYvOh5gssgofrhDT6FJQ+NhIexrgmCnxdEZPXw5b5FwEGVBA6trWkCQQCaW76dm5qMDcYGnRsHCu+78aqx+8Q/tJJ5AdM4yOFlF3eC+EEjKxCd/hKpgYtWAK0GroQHygBUMns/sjJ5nqnHAkEAhSXh9aq+JHrjm6/JewNtpmPU0ZpUIwnuIUITkJ5eSGdZJ7YpKQ/g8e+RWONzJaBpNJDuAfYQL7xjpEOKjSBF4QJAcOTA2QzBT8WTG6sz5Ua+Z7ssmNgGhIFQz2fiUBm7n/A13HgtS8dnc+YrSAYTHmeIJObmcbvv2aTCN6fg+spsaQJAWtvKHj7NNIPfc1cboOe1z+wShUwC0w/kOgb/lK3FDz8a7z2YRcAFxRUair0OQzWFl2YsPISfON4OGYbwWqnJvg==";
}
