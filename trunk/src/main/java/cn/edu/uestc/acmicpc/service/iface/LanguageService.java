package cn.edu.uestc.acmicpc.service.iface;

import cn.edu.uestc.acmicpc.db.dto.impl.language.LanguageDTO;
import cn.edu.uestc.acmicpc.db.entity.Language;

import java.util.List;

/**
 * Description
 * TODO(mzry1992)
 */
public interface LanguageService extends DatabaseService<Language, Integer> {

  public List<LanguageDTO> getLanguageList();

  public String getExtension(Integer languageId);

  public String getLanguageName(Integer languageId);
}
