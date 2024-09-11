package org.example.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;

/**
 * CSVファイルのvalidationチェックを行うクラス.
 */
@Setter
@Getter
public class CsvUtil {

  // アップロードされたCSVファイル
  private MultipartFile file;

  // どのフォーマットを用いてフォーマットチェックを行うのか判別するフラグ
  // flagの種類はValidationMessage.propertiesを参照
  private String flag;

  MessageSource messageSource;

  /**
   * CsvUtilクラスのコンストラクタ.
   *
   * @param file CSVファイル
   * @param flag 判別フラグ
   * @param messageSource messageSource
   */
  public CsvUtil(MultipartFile file, String flag, MessageSource messageSource) {
    this.file = file;
    this.flag = flag;
    this.messageSource = messageSource;
  }

  /**
   * アップロードされたCSVファイルのvalidationチェックを行う.
   *
   * @return String型のlist エラーメッセージ
   */
  public List<String> checkCsvFileValidation() {

    // フォーマット以外のvalidationチェック
    List<String> errorList = checkCsvFileValidation(file);
    // ファイルのフォーマット以外に問題がある場合、フォーマットチェックをやる必要がないのでreturn
    if (!errorList.isEmpty()) {
      return errorList;
    }

    try (InputStream inputStream = file.getInputStream();
         CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {

      // フォーマットのvalidationチェック
      List<String[]> readCsvList = csvReader.readAll();
      errorList = checkCsvFileValidation(readCsvList, flag);

      return errorList;

    } catch (IOException | CsvException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * CSVファイルのフォーマットチェック以外のvalidationチェックを行う.
   *
   * @param file CSVファイル
   * @return String型のlist エラーメッセージ
   */
  private List<String> checkCsvFileValidation(MultipartFile file) {

    List<String> errorList = new ArrayList<>();

    // ファイルがアップロードされているかチェック
    if (file == null) {
      errorList.add(messageSource.getMessage("errMsg.notSelectedFile", null, Locale.getDefault()));

      // ファイルがnullだと下記のチェックをやる必要がないのでreturn
      return errorList;
    }

    // CSVファイルかチェック
    String fileName = file.getOriginalFilename();

    if (fileName != null) {
      String fileExtension = fileName.substring(fileName.lastIndexOf('.'));

      if (!fileExtension.equals(".csv")) {
        errorList.add(
            messageSource.getMessage("errMsg.notSelectedFile", null, Locale.getDefault()));
      }
    }

    // ファイルサイズのチェック
    if (file.getSize() > Integer.parseInt(messageSource.getMessage("maxFileSize",
        null, Locale.getDefault()))) {
      errorList.add(messageSource.getMessage("errMsg.tooLargeFile", null, Locale.getDefault()));
    }
    return errorList;
  }

  /**
   * CSVファイルのフォーマットチェックを行う.
   *
   * @param readCsvList 読み込んだCSVファイルのlist
   * @param flag どのフォーマットを用いてフォーマットチェックを行うのか判別するフラグ
   * @return String型のlist エラーメッセージ
   * @throws CsvException 例外
   * @throws IOException 例外
   */
  private List<String> checkCsvFileValidation(List<String[]> readCsvList, String flag)
      throws CsvException, IOException {

    List<String> errorList = new ArrayList<>();
    String header = "";
    int comma = 0;

    if ("itemMaster".equals(flag)) {
      header = messageSource.getMessage("itemMaster.header", null, Locale.getDefault());
      comma = Integer.parseInt(messageSource.getMessage("itemMaster.formatComma",
          null, Locale.getDefault()));
    }

    // ヘッダー行のチェック
    if (!header.equals(Arrays.toString(readCsvList.get(0)))) {
      errorList.add(messageSource.getMessage("errMsg.mismatchFormat", null, Locale.getDefault()));
      /*
       * 指定のヘッダーがない + 規定のフォーマットに定められたカンマの数ではないファイルがアップロードされたとき
       * 二重にエラーメッセージを表示してしまうので、
       * それを防ぐためのreturn
       */
      return errorList;
    }

    // カンマの数のチェック
    for (String[] line : readCsvList) {
      if (countComma(Arrays.toString(line)) != comma) {
        errorList.add(messageSource.getMessage("errMsg.mismatchFormat", null, Locale.getDefault()));
        break;
      }
    }
    return errorList;
  }

  /**
   * csvファイルから読み込んだデータに含まれるカンマの数を数える.
   *
   * @param line csvファイルから読み込んだデータ
   * @return カンマの数
   */
  private static long countComma(String line) {
    return line.chars()
        .filter(c -> c == ',')
        .count();
  }
}
