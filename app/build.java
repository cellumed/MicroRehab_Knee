public class build extends groovy.lang.Script {
    public static void main(java.lang.String[] args) {
        new build(new groovy.lang.Binding(args)).run();
    }

    public java.lang.Object run() {
        java.util.LinkedHashMap<java.lang.String, java.lang.String> map = new java.util.LinkedHashMap<java.lang.String, java.lang.String>(1);
        map.put("plugin", "com.android.application");
        apply(map);

        android(new groovy.lang.Closure<com.android.build.gradle.internal.CompileOptions>(this, this) {
            public com.android.build.gradle.internal.CompileOptions doCall(java.lang.Object it) {
                compileSdkVersion(23);
                buildToolsVersion("23.0.3");

                defaultConfig(new groovy.lang.Closure<com.android.build.gradle.internal.dsl.JackOptions>(build.this, build.this) {
                    public com.android.build.gradle.internal.dsl.JackOptions doCall(com.android.build.gradle.internal.dsl.ProductFlavor it) {
                        setApplicationId(new java.lang.Object[]{"com.cellumed.ems_ui"});
                        minSdkVersion(15);
                        targetSdkVersion(23);
                        setVersionCode(new java.lang.Object[]{1});
                        setVersionName(new java.lang.Object[]{"1.0"});
                        return jackOptions(new groovy.lang.Closure<java.lang.Object>(build.this, build.this) {
                            public java.lang.Object doCall(java.lang.Object it) {
                                return enabled(new java.lang.Object[]{true});
                            }

                            public java.lang.Object doCall() {
                                return doCall(null);
                            }

                        });
                    }

                    public JackOptions doCall() {
                        return doCall(null);
                    }

                });


                buildTypes(new groovy.lang.Closure<com.android.build.gradle.internal.dsl.BuildType>(build.this, build.this) {
                    public com.android.build.gradle.internal.dsl.BuildType doCall(org.gradle.api.NamedDomainObjectContainer<com.android.build.gradle.internal.dsl.BuildType> it) {
                        return release(new groovy.lang.Closure<java.lang.Object>(build.this, build.this) {
                            public java.lang.Object doCall(java.lang.Object it) {
                                setMinifyEnabled(new java.lang.Object[]{false});
                                return proguardFiles(new java.lang.Object[]{invokeMethod("getDefaultProguardFile", new java.lang.Object[]{"proguard-android.txt"}), "proguard-rules.pro"});
                            }

                            public java.lang.Object doCall() {
                                return doCall(null);
                            }

                        });
                    }

                    public BuildType doCall() {
                        return doCall(null);
                    }

                });
                return compileOptions(new groovy.lang.Closure<java.lang.Object>(build.this, build.this) {
                    public java.lang.Object doCall(java.lang.Object it) {
                        setSourceCompatibility(new java.lang.Object[]{org.gradle.api.JavaVersion.VERSION_1_8});
                        return setTargetCompatibility(new java.lang.Object[]{org.gradle.api.JavaVersion.VERSION_1_8});
                    }

                    public java.lang.Object doCall() {
                        return doCall(null);
                    }

                });
            }

            public CompileOptions doCall() {
                return doCall(null);
            }

        });

        dependencies(new groovy.lang.Closure<java.lang.Object>(this, this) {
            public java.lang.Object doCall(java.lang.Object it) {
                compile(new java.lang.Object[]{"com.android.support:appcompat-v7:23.3.0"});
                return compile(new java.lang.Object[]{"com.jakewharton:butterknife:7.0.1"});
            }

            public java.lang.Object doCall() {
                return doCall(null);
            }

        });
        return null;

    }

    public build(Binding binding) {
        super(binding);
    }

    public build() {
        super();
    }
}
