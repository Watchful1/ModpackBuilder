package gr.watchful.packbuilder.moddetection;

import gr.watchful.packbuilder.datastructures.modrelated.ModFile;
import gr.watchful.packbuilder.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.objectweb.asm.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModFileParser {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // We need a central place to add ID's for when we can't return what we want
    private ModFile workingMod;
    private int rawClasses;

    public ArrayList<ModFile> discoverMods(File folder) {
        rawClasses = 0;
        ArrayList<ModFile> mods = new ArrayList<>();

        if(folder == null || !folder.exists()) return mods;

        ModFile temp;
        if(folder.isFile()) {
            temp = processFile(folder);
            if (temp != null) {
                mods.add(temp);
            }
        } else {
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) mods.addAll(discoverMods(file));
                else {
                    temp = processFile(file);
                    if (temp != null) {
                        mods.add(temp);
                    }
                }
            }
        }

        if(rawClasses > 0) logger.warning("Raw class files are present in the mods folder\n"+
                "This is not supported by this tool");
        return mods;
    }

    public ModFile processFile(File modArchive) {
        workingMod = new ModFile(modArchive);

        String ext = FileUtils.getFileExtension(modArchive);

        if(ext != null && (ext.equals("jar") || ext.equals("zip") || ext.equals("litemod") || ext.equals("disabled"))) {
            try {
                ZipFile file = new ZipFile(modArchive);
                Enumeration<? extends ZipEntry> files = file.entries();

                boolean hasClassFiles = false;

                while(files.hasMoreElements()) {
                    ZipEntry item = files.nextElement();
                    String itemName = item.getName();

                    if(itemName.equals("litemod.json") || itemName.endsWith("info")) {
                        BufferedReader streamReader = new BufferedReader(new InputStreamReader(file.getInputStream(item), "UTF-8"));
                        StringBuilder responseStrBuilder = new StringBuilder();

                        String inputStr;
                        while ((inputStr = streamReader.readLine()) != null)
                            responseStrBuilder.append(inputStr);
                        if(itemName.equals("litemod.json")) {
                            try {
                                JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
                                String name = (String) jsonObject.get("name");
                                workingMod.addID(name);
                            } catch (JSONException e) {
                                continue;
                            }
                        }
                        if(itemName.endsWith("info")) {
                            try {
                                JSONArray jsonArray = new JSONArray(responseStrBuilder.toString());
                                workingMod.mcmodVersion = (String) jsonArray.getJSONObject(0).get("version");
                                workingMod.mcmodID = (String) jsonArray.getJSONObject(0).get("modid");
                                workingMod.mcmodName = (String) jsonArray.getJSONObject(0).get("name");
                            } catch (JSONException e) {
                                try {
                                    // Version 2
                                    JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
                                    JSONArray jsonArray = jsonObject.getJSONArray("modList");
                                    workingMod.mcmodVersion = (String) jsonArray.getJSONObject(0).get("version");
                                    workingMod.mcmodID = (String) jsonArray.getJSONObject(0).get("modid");
                                    workingMod.mcmodName = (String) jsonArray.getJSONObject(0).get("name");
                                    // assuming there's only one mod in mcmod.info. I can't think of an easy way to support multiple mod versions at this point
                                } catch (JSONException e2) {
                                    continue;
                                }
                                continue;
                            }
                        }
                    }
                    if(itemName.equals("META-INF/MANIFEST.MF")) {
                        BufferedReader streamReader = new BufferedReader(new InputStreamReader(file.getInputStream(item), "UTF-8"));

                        String inputStr;
                        while ((inputStr = streamReader.readLine()) != null) {
                            if(inputStr.startsWith("TweakClass:")) {
                                workingMod.addID(inputStr.split(" ")[1]);
                            } else if(inputStr.startsWith("Main-Class:")) {
                                workingMod.addID(inputStr.split(" ")[1]);
                            }
                        }
                    }
                    if(item.isDirectory() || !itemName.endsWith("class")) continue;
                    hasClassFiles = true;

                    ClassReader reader;
                    try {
                        reader = new ClassReader(file.getInputStream(item));
                    } catch (Exception e) {
                        continue;
                    }
                    try {
                        reader.accept(new ModClassVisitor(), 0);
                    } catch (Exception e) {
                        // do nothing
                    }
                }
                file.close();
                if(hasClassFiles) {
                    return workingMod;
                } else {
                    return null;
                }
            } catch (IOException e) {
                return workingMod;
            }
        } else if(ext != null && ext.equals("class")) {
            rawClasses++;
        }
        return null;
    }

    public class ModClassVisitor extends ClassVisitor {
        boolean tmp;

        public ModClassVisitor() {
            super(Opcodes.ASM4);
            tmp = false;
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            //logger.info(name);
            if(superName.equals("BaseMod")) {
                workingMod.addID(name);
            } else if(superName.equals("DummyModContainer") || superName.equals("cpw/mods/fml/common/DummyModContainer")) {
                tmp = true;
                workingMod.addID(name);
            }
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, boolean runtime) {
            if (name.equals("Lcpw/mods/fml/common/Mod;") || name.equals("Lnet/minecraftforge/fml/common/Mod;") ||
                    name.equals("Lorg/spongepowered/api/plugin/Plugin;")) {
                return new ModAnnotationVisitor();
            } else {
                return new AnnotationVisitor(Opcodes.ASM4) {};
            }
        }

        @Override
        public  MethodVisitor visitMethod(int access, String name, String desc,
                                          String signature, String[] exceptions) {
            if (tmp) {
                return new ModMethodVisitor();
            }
            return null;
        }
    }

    public class ModAnnotationVisitor extends AnnotationVisitor {
        String id;
        String version;

        public ModAnnotationVisitor() {
            super(Opcodes.ASM4);
        }

        @Override
        public void visit(String key, Object value) {
            if(key.equals("modid") || key.equals("id")) {
                id = value.toString();
            } else if(key.equals("version")) {
                version = value.toString();
            }
        }

        @Override
        public void visitEnum(String name, String desc, String value) {

        }

        @Override
        public void visitEnd() {
            if(id != null) workingMod.addID(id);
            if(version != null) workingMod.codeVersion.add(version);
        }
    }

    public class ModMethodVisitor extends MethodVisitor {
        String temp;
        String id;
        String version;
        boolean recent = false;

        public ModMethodVisitor() {
            super(Opcodes.ASM4);
        }

        @Override
        public void visitFieldInsn(int opc, String owner, String name, String desc) {
            if(name.equals("modId")) {
                id = temp;
            } else if(name.equals("version") && recent) {
                version = temp;
            } else {
                recent = false;
            }
        }

        @Override
        public void visitLdcInsn(Object cst) {
            temp = cst.toString();
            recent = true;
        }

        @Override
        public void visitEnd() {
            if(id != null) workingMod.addID(id);
            if(version != null) workingMod.codeVersion.add(version);
        }
    }
}
